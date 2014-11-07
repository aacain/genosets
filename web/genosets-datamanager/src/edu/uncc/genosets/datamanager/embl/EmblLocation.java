/*
 * Copyright (C) 2014 Aurora Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.uncc.genosets.datamanager.embl;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aacain
 */
public class EmblLocation {

    private final String locationString;
    private Group group;
    private HashMap<String, Mapping> mapValue;

    public EmblLocation(String locationString) {
        this.locationString = locationString;
    }

    public Group getGroup() {
        if (this.group == null) {
            buildGroups();
        }
        return this.group;
    }

    private void buildGroups() {
        this.group = new Group();
        String regex = "(unk\\d+)|([A-Z]+\\d+\\.\\d+)|([A-Z]+\\d+)|[a-zA-Z]+|\\d+|[\\(\\),]|[<>^:]|\\.+";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(locationString);
        List<String> split = new LinkedList<String>();
        while (m.find()) {
            String match = m.group();
            split.add(match);
        }
        buildGroups(group, split, 0);
    }

    private void buildGroups(Group group, List<String> list, int index) {
        if (list.size() <= index + 1) {
            return;
        }
        Group myGroup = group;
        int updatedIndex = index;
        String expression = list.get(index);
        if (expression.equals("gap")) {
            Position position = new Position();
            updatedIndex = getGap(position, list, index + 1);
            myGroup = new Group();
            group.addChild(myGroup);
            myGroup.setValue(expression);
            myGroup.setOperator(expression);
            myGroup.setPosition(position);
        } else if (Pattern.compile("[A-Za-z]+(?!\\d)").matcher(expression).matches()) {
            //operator such as join, complement, or order
            myGroup = new Group();
            group.addChild(myGroup);
            myGroup.setValue(expression);
            myGroup.setOperator(expression);
            group = myGroup;
        } else if ((")").equals(expression)) {
            group = myGroup.getParent();
        } else if (Pattern.compile("(([A-Z]+\\d+\\.\\d+)|([A-Z]+\\d+))").matcher(expression).matches()) {
            //remote accession
            Position position = new Position();
            updatedIndex = addPosition(position, list, index);
            myGroup = new Group();
            group.addChild(myGroup);
            myGroup.setValue(expression);
            myGroup.setPosition(position);
        } else if (Pattern.compile("[<>]|\\d+").matcher(expression).matches()) {
            //position
            Position position = new Position();
            updatedIndex = addPosition(position, list, index);
            myGroup = new Group();
            group.addChild(myGroup);
            myGroup.setValue(expression);
            myGroup.setPosition(position);
        } else {
            
        }
        updatedIndex++;
        buildGroups(group, list, updatedIndex);
    }

    private int getGap(Position position, List<String> list, int index) {
        position.setIsGap(true);
        for (int updatedIndex = index; updatedIndex < list.size(); updatedIndex++) {
            String expression = list.get(updatedIndex);
            if (Pattern.compile("\\d+").matcher(expression).matches()) {
                position.setStartPosition(1);
                position.setEndPosition(Integer.parseInt(expression));
            } else if (expression.startsWith("unk")) {
                position.setStartPosition(1);
                position.setRangeOperator(Position.RangeOperator.GAP_UNKNOWN);
                String[] split = expression.split("unk");
                for (String string : split) {
                    if (string.length() > 0) {
                        position.setEndPosition(Integer.parseInt(string));
                    }
                }
            } else if (expression.equals("(")) {
                //ignore
            } else if (expression.equals(")")) {
                return updatedIndex;
            } else {
                updatedIndex--;
                return updatedIndex;
            }
        }
        return index;
    }

    private int addPosition(Position position, List<String> list, int index) {
        boolean onUpperBoundary = false;
        for (int updatedIndex = index; updatedIndex < list.size(); updatedIndex++) {
            String expression = list.get(updatedIndex);
            if (Pattern.compile("(([A-Z]+\\d+\\.\\d+)|([A-Z]+\\d+))").matcher(expression).matches()) {
                //on accession
                position.accession = expression;
            } else if (Pattern.compile("\\d+").matcher(expression).matches()) {
                if (onUpperBoundary) {
                    position.setEndPosition(Integer.parseInt(expression));
                } else {
                    position.setStartPosition(Integer.parseInt(expression));
                    position.setEndPosition(position.getStartPosition());
                }
            } else if (("<").equals(expression)) {
                //less than
                if (onUpperBoundary) {
                    position.setEndOperator(Position.PositionOperator.LESS_THAN);
                } else {
                    position.setStartOperator(Position.PositionOperator.LESS_THAN);
                }
            } else if ((">").equals(expression)) {
                //greater than
                if (onUpperBoundary) {
                    position.setEndOperator(Position.PositionOperator.GREATER_THAN);
                } else {
                    position.setEndOperator(Position.PositionOperator.LESS_THAN);
                }
            } else if ((".").equals(expression)) {
                onUpperBoundary = true;
                position.setRangeOperator(Position.RangeOperator.BETWEEN_EXACT_UKNOWN);
            } else if (("..").equals(expression)) {
                onUpperBoundary = true;
                position.setRangeOperator(Position.RangeOperator.CONTIGUOUS);
            } else if (("^").equals(expression)) {
                onUpperBoundary = true;
                position.setRangeOperator(Position.RangeOperator.BETWEEN);
            } else if ((":").equals(expression)) {
                //swallow
            } else {
                updatedIndex--;
                return updatedIndex;
            }
        }
        return index;
    }

    public String asEmblString() {
        return asEmblString(getGroup()).toString();
    }

    private StringBuilder asEmblString(Group group) {
        StringBuilder bldr = new StringBuilder();
        //print me
        if (group.getOperator() != null) {
            bldr.append(group.getOperator()).append("(");
        }

        if (group.getPosition() != null) {
            if (group.getPosition().isGap()) {
                if (group.getPosition().getRangeOperator() == Position.RangeOperator.GAP_UNKNOWN) {
                    bldr.append("unk");
                }
                bldr.append(group.getPosition().getEndPosition());
            } else {
                if (group.getPosition().accession != null) {
                    bldr.append(group.getPosition().accession).append(":");
                }
                //print the start position
                bldr.append(Position.positionOperator(group.getPosition().getStartOperator())).append(group.getPosition().startPosition);
                if (group.getPosition().getRangeOperator() != Position.RangeOperator.NONE) {
                    bldr.append(Position.rangeOperator(group.getPosition().getRangeOperator()));
                    bldr.append(Position.positionOperator(group.getPosition().getEndOperator())).append(group.getPosition().endPosition);
                }
            }
        }

        //visit children
        for (int i = 0; i < group.getChildren().size(); i++) {
            if (group.getOperator() != null && i > 0) {
                bldr.append(",");
            }
            bldr.append(asEmblString(group.getChildren().get(i)));
        }

        if (group.getOperator() != null) {
            bldr.append(")");
        }

        return bldr;
    }

    public Mapping getMapping(String accession, int position) throws IOException {
        if (mapValue == null) {
            MapValue mv = new MapValue();
            buildLookupMaps(getGroup(), mv, 1);
            this.mapValue = mv.map;
        }
        Mapping wholeMapping = this.mapValue.get(accession);
        if (wholeMapping == null) {
            throw new IOException("Accession " + accession + " could not be found");
        }
        if (position >= wholeMapping.min && position <= wholeMapping.max) {
            if (wholeMapping.isComplement) {
                return new Mapping(wholeMapping.mappedIndex - (position - wholeMapping.offset), wholeMapping.offset, wholeMapping.isComplement, wholeMapping.min, wholeMapping.max);
            } else {
                return new Mapping(wholeMapping.mappedIndex + (position - wholeMapping.offset), wholeMapping.offset, wholeMapping.isComplement, wholeMapping.min, wholeMapping.max);
            }
        } else {
            throw new IOException("The position " + position + " for accession " + accession + " is out of range [" + wholeMapping.min + "," + wholeMapping.max + "]");
        }
    }

    private int buildLookupMaps(Group group, MapValue mv, int seqIndex) {
        MapValue childmv = new MapValue();
        childmv.segmentStart = seqIndex;
        for (Group child : group.getChildren()) {
            seqIndex = buildLookupMaps(child, childmv, seqIndex);
        }
        if (group.getPosition() != null) {
            mv.positionList.add(group.getPosition());
            if (group.getPosition().getAccession() != null) {
                Mapping mapping = new Mapping(seqIndex, group.getPosition().getStartPosition(), false, group.getPosition().getStartPosition(), group.getPosition().getEndPosition());
                childmv.map.put(group.getPosition().getAccession(), mapping);
            }
            //mv.seqValue = mv.seqValue + (group.getPosition().getEndPosition() - group.getPosition().getStartPosition() + 1);
            //childmv.seqValue = childmv.seqValue + (group.getPosition().getEndPosition() - group.getPosition().getStartPosition() + 1);
            seqIndex = seqIndex + (group.getPosition().getEndPosition() - group.getPosition().getStartPosition() + 1);
            //mv.segmentLength = mv.segmentLength + (group.getPosition().getEndPosition() - group.getPosition().getStartPosition() + 1);
            
        }
        if (group.getOperator() != null && group.getOperator().equals("complement")) {
            for (Mapping mapping : childmv.map.values()) {
                //int length = seqIndex - mv.segmentStart;
                int length = seqIndex - childmv.segmentStart;
                if(mapping.isIsComplement()){
                    mapping.mappedIndex = mapping.mappedIndex - length + 1;
                }else{
                    mapping.mappedIndex = seqIndex - 1 - (mapping.mappedIndex - childmv.segmentStart);
                }
                mapping.isComplement = !mapping.isComplement;
            }
        }
        mv.map.putAll(childmv.map);

        return seqIndex;
    }

    public static class Group {

        private Group parent;
        private List<Group> children = new LinkedList<Group>();
        private String operator;
        private String value;
        private Position position;

        public void addChild(Group group) {
            children.add(group);
            group.parent = this;
        }

        public List<Group> getChildren() {
            return this.children;
        }

        public Group getParent() {
            return parent;
        }

        public void setParent(Group parent) {
            this.parent = parent;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Position getPosition() {
            return position;
        }

        public void setPosition(Position position) {
            this.position = position;
        }
    }

    private static class Position {

        String accession;
        int startPosition;
        int endPosition;
        boolean gap;
        PositionOperator startOperator = PositionOperator.EXACT;
        PositionOperator endOperator = PositionOperator.EXACT;
        RangeOperator rangeOperator = RangeOperator.NONE;

        public Position() {
        }

        public Position(int startPosition, int endPosition) {
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }

        public enum RangeOperator {

            NONE,
            CONTIGUOUS, //..
            BETWEEN, //^
            BETWEEN_EXACT_UKNOWN, //.
            GAP_UNKNOWN //gap(unk100)
        }

        public enum PositionOperator {

            LESS_THAN, GREATER_THAN, EXACT
        }

        public String getAccession() {
            return accession;
        }

        public void setAccession(String accession) {
            this.accession = accession;
        }

        public int getStartPosition() {
            return startPosition;
        }

        public void setStartPosition(int startPosition) {
            this.startPosition = startPosition;
        }

        public int getEndPosition() {
            return endPosition;
        }

        public void setEndPosition(int endPosition) {
            this.endPosition = endPosition;
        }

        public PositionOperator getStartOperator() {
            return startOperator;
        }

        public void setStartOperator(PositionOperator startOperator) {
            this.startOperator = startOperator;
        }

        public PositionOperator getEndOperator() {
            return endOperator;
        }

        public void setEndOperator(PositionOperator endOperator) {
            this.endOperator = endOperator;
        }

        public RangeOperator getRangeOperator() {
            return rangeOperator;
        }

        public void setRangeOperator(RangeOperator rangeOperator) {
            this.rangeOperator = rangeOperator;
        }

        public boolean isGap() {
            return gap;
        }

        public void setIsGap(boolean isGap) {
            this.gap = isGap;
        }

        public static String rangeOperator(RangeOperator operator) {
            switch (operator) {
                case NONE:
                    return "";
                case BETWEEN:
                    return "^";
                case BETWEEN_EXACT_UKNOWN:
                    return ".";
                case CONTIGUOUS:
                    return "..";
                default:
                    return "?";
            }
        }

        public static String positionOperator(PositionOperator operator) {
            switch (operator) {
                case EXACT:
                    return "";
                case GREATER_THAN:
                    return ">";
                case LESS_THAN:
                    return "<";
                default:
                    return "?";
            }
        }
    }

    public static class Mapping {

        int mappedIndex;
        int offset;
        boolean isComplement;
        int min;
        int max;

        public Mapping() {
        }

        public Mapping(int mappedIndex, int offset, boolean isComplement) {
            this(mappedIndex, offset, isComplement, 0, 0);
        }

        public Mapping(int mappedIndex, int offset, boolean isComplement, int min, int max) {
            this.mappedIndex = mappedIndex;
            this.offset = offset;
            this.isComplement = isComplement;
            this.min = min;
            this.max = max;
        }

        public int getMappedIndex() {
            return mappedIndex;
        }

        public void setMappedIndex(int mappedIndex) {
            this.mappedIndex = mappedIndex;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public boolean isIsComplement() {
            return isComplement;
        }

        public void setIsComplement(boolean isComplement) {
            this.isComplement = isComplement;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }

    private static class MapValue {

        List<Position> positionList = new LinkedList();
        HashMap<String, Mapping> map = new HashMap<String, Mapping>();
        //int segmentLength = 0;
        int segmentStart = 0;
    }
}
