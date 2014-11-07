package edu.uncc.genosets.geneontology.obo;

import edu.uncc.genosets.geneontology.api.GeneOntology;
import java.io.*;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.text.DecimalFormat;
import java.util.HashMap;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

public class OboDataObject {

    private SoftReference<Obo> oboRef;
    private String url;
    private String localUrl;
    private FileObject fileObject;
    public final static String PROP_DATE = "date";

    public OboDataObject(String url) {
        this(url, null);
    }

    public OboDataObject(FileObject fo) {
        this(null, fo);
    }

    public OboDataObject(String url, FileObject fo) {
        this.url = url;
        this.fileObject = fo;
        if(fo != null){
            this.localUrl = fo.getPath();
            if(url == null){
                this.url = this.localUrl;
            }
        }else{// must have a url
            if(!url.startsWith("http://")){
                this.localUrl = url;
            }//will set local local url later when downloaded
        }
    }

    protected Obo parse() throws IOException {
        FileObject myFileObject = getFileObject();
        if (myFileObject == null) {
            throw new IOException("Unable to locate file.");
        }
        HashMap<String, Term> termMap = new HashMap<String, Term>();
        BufferedReader br = null;
        br = new BufferedReader(new InputStreamReader(myFileObject.getInputStream()));

        String line = null;
        Term current = new Term();
        while ((line = br.readLine()) != null) {
            if (line.startsWith("id:")) {
                String id = line.split("id: ")[1];
                current = termMap.get(id);
                if (current == null) {
                    current = new Term();
                    current.setGoId(id);
                    termMap.put(current.getGoId(), current);
                }
            } else if (line.startsWith(("is_a: "))) {
                String[] split = line.split(" ");
                String parentId = split[1];
                Term parent = termMap.get(parentId);
                if (parent == null) {
                    parent = new Term(parentId);
                    termMap.put(parentId, parent);
                }
                //create relationship
                current.addParent(parent, "is_a");
            } else if (line.startsWith(("relationship: "))) {
                String[] split = line.split(" ");
                String relation = split[1];
                String parentId = split[2];
                Term parent = termMap.get(parentId);
                if (parent == null) {
                    parent = new Term(parentId);
                    termMap.put(parentId, parent);
                }
                //create relationship
                current.addParent(parent, relation);
            } else if (line.startsWith(("alt_id:"))) {
                String[] split = line.split(" ");
                String altId = split[1];
                termMap.put(altId, current);
            } else if (line.startsWith("name:")) {
                String[] split = line.split("name:");
                String name = split[1];
                current.setName(name);
            } else if (line.startsWith("is_obsolete: true")) {
                current.setIsObsolete(Boolean.TRUE);
            }
        }
        //add undefined
        Term unclass = new Term(GeneOntology.UNCLASSIFIED_TERMID);
        unclass.setName("Unclassified");
        termMap.put(unclass.getGoId(), unclass);

        //add obsolete
        Term obsolete = new Term(GeneOntology.OBSOLETE_TERMID);
        obsolete.setName("Obsolete");
        termMap.put(obsolete.getGoId(), obsolete);

        Obo obo = new Obo(this, termMap);
        return obo;
    }

    public synchronized Obo getObo() {
        if (oboRef == null || oboRef.get() == null) {
            try {
                Obo obo = parse();
                oboRef = new SoftReference<Obo>(obo);
                OboManager.setLastUsed(this);
                return obo;
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return oboRef.get();
    }

    public FileObject getFileObject() throws IOException {
        return getFileObject(null);
    }

    public FileObject getFileObject(ProgressHandle handle) throws IOException {
        if (fileObject != null) {
            return fileObject;
        }
        if (url.startsWith("http://")) {
            fileObject = copyRemoteFile(handle);
        } else {
            fileObject = getLocalFile();
        }
        this.localUrl = FileUtil.toFile(fileObject).getAbsolutePath();
        OboManager.setLastUsed(this);
        return fileObject;
    }

    private FileObject getLocalFile() throws IOException {
        return FileUtil.toFileObject(new File(url));
    }

    private FileObject copyRemoteFile(ProgressHandle handle) throws IOException {
        DecimalFormat decimal = new DecimalFormat("0.0");
        FileObject fo = FileUtil.createData(FileUtil.getConfigRoot(), "obo-last.obo");
        ReadableByteChannel src = null;
        WritableByteChannel dest = null;
        try {
            final URL website = new URL(url);
            final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
            src = Channels.newChannel(website.openStream());
            dest = Channels.newChannel(new FileOutputStream(FileUtil.toFile(fo)));
            double totalRead = 0;
            int readBytes = 0;
            while ((readBytes = src.read(buffer)) != -1) {
                totalRead = totalRead + readBytes;
                System.out.println(buffer.position());
                if (handle != null) {
                    handle.progress("Reading Obo. " + decimal.format(totalRead / (double) (1024 * 1024)) + "Mb read.");
                }
                // prepare the buffer to be drained
                buffer.flip();
                // write to the channel, may block
                dest.write(buffer);
                // If partial transfer, shift remainder down
                // If buffer is empty, same as doing clear()
                buffer.compact();
            }
            // EOF will leave buffer in fill state
            buffer.flip();
            // make sure the buffer is fully drained.
            while (buffer.hasRemaining()) {
                dest.write(buffer);
            }
        } finally {
            try {
                src.close();
                dest.close();
            } catch (IOException ex) {
            }
        }

        return fo;
    }

    public String getUrl() {
        return url;
    }

    protected String getLocalUrl() {
        return this.localUrl;
    }
}
