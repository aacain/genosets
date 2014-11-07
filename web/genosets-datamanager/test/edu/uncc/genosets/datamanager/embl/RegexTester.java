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
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;

/**
 *
 * @author lucy
 */
public class RegexTester {

    @Test
    public void test3() throws IOException {
        String string = //"join(complement(A1:1..20), join(complement(B1:5..15), C1:17..25))";
                //"complement(join(A1:1..10, B1:6..15, C1:1..10))";
                "join(complement(A1:1..10), complement(B1:6..15), C1:1..10)";
        //"join(J16732:50,gap(unk100),complement(<113..>500), gap(500),445, 102.>110, 123^124, join(1..495, 500.500), J16543.50:444.445)";
        List<Integer> startParenList = new LinkedList<Integer>();
        List<Integer> endParenList = new LinkedList<Integer>();
        String regex = "(unk\\d+)|([A-Z]+\\d+\\.\\d+)|([A-Z]+\\d+)|[a-zA-Z]+|\\d+|[\\(\\),]|[<>^:]|\\.+";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(string);
        List<String> split = new LinkedList<String>();
        int i = 0;
        while (m.find()) {
            String match = m.group();
            split.add(match);
            if (match.equals("(")) {
                startParenList.add(i - 1);
            } else if (match.equals(")")) {
                endParenList.add(i);
            }
            i++;
        }
        Group group = new Group();
        buildGroups(group, split, 0);
        System.out.println(groupAsString(group));
        List<Position> positionList = buildList(group);
        for (Position position : positionList) {
            System.out.println(position.getStartPosition() + "-" + position.getEndPosition());
        }
        //buildMaps(group);
        //buildChildren(group, new LinkedList(), new HashMap<String, Mapping>(), 1);
        MapValue mv = new MapValue();
        buildList2(group, mv, 1);
        String acc = "C1";
        int value = 1;
        Mapping get = mv.map.get(acc);
        if(get.isComplement){
            System.out.println("complement index is " + (get.mappedIndex - (value - get.offset)));
        }else{
            System.out.println("index is " + (get.mappedIndex + (value - get.offset)));
        }
    }
    
    private void getMappedIndex(){
        
    }

    private int buildChildren(Group group, List<Position> list, HashMap<String, Mapping> map, int seqIndex) {
        for (Group child : group.getChildren()) {
            seqIndex = buildChildren(child, list, map, seqIndex);
        }
        if (group.getPosition() != null) {
            list.add(group.getPosition());
        }
        int totalLength = 0;
        for (Position position : list) {
            totalLength = totalLength + (position.getEndPosition() + position.getStartPosition() + 1);
            if (!position.isGap()) {
                Mapping mapping = new Mapping(seqIndex, position.getStartPosition(), false);
                if (position.getAccession() != null) {
                    map.put(position.getAccession(), mapping);
                }
            }
        }
        if (group.getOperator() != null && group.getOperator().equals("complement")) {
        }
        return seqIndex + totalLength;
    }

    //@Test
    public void test4() throws IOException {
        String string =
                //"join(join(join(join(866..877), 655..666), 455..555),complement(<113..>5), 445, 102.110, 123^124, J16543.50:444.445)";
                "join(complement(4918..5163),gap(unk100), gap(501),complement(2691..4571))";
        List<Integer> startParenList = new LinkedList<Integer>();
        List<Integer> endParenList = new LinkedList<Integer>();
        String regex = "(unk\\d+)|([A-Z]+\\d+\\.\\d+)|([A-Z]+\\d+)|[a-zA-Z]+|\\d+|[\\(\\),]|[<>^:]|\\.+";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(string);
        List<String> split = new LinkedList<String>();
        int i = 0;
        while (m.find()) {
            String match = m.group();
            split.add(match);
            if (match.equals("(")) {
                startParenList.add(i - 1);
            } else if (match.equals(")")) {
                endParenList.add(i);
            }
            i++;
        }
    }

    //@Test
    public void testMap() {
        NavigableMap<Integer, Position> map = new TreeMap<Integer, Position>();
        map.put(0, new Position(0, 100));
        map.put(101, new Position(101, 600));
        int lookup = 601;
        Map.Entry<Integer, Position> entry = map.floorEntry(lookup);

        if (entry == null) {
            System.out.println(lookup + "not found.");
        } else if (lookup <= entry.getValue().getEndPosition()) {
            System.out.println("Found " + lookup + " at position " + entry.getValue().getStartPosition() + "-" + entry.getValue().getEndPosition());
        } else {
            System.out.println(lookup + " found but out of range of " + entry.getValue().getEndPosition());
        }
    }

    private void buildMaps(Group group) {
        List<Position> positionList = buildList(group);
        NavigableMap<Integer, Position> rangeMap = new TreeMap<Integer, Position>();
        HashMap<String, Mapping> offsetMap = new HashMap<String, Mapping>();
        int seqIndex = 1;
        for (Position pos : positionList) {
            rangeMap.put(seqIndex, pos);
            Mapping mapping = new Mapping();
            mapping.offset = pos.getStartPosition();
            mapping.mappedIndex = seqIndex;
            seqIndex = pos.getEndPosition() - pos.getStartPosition() + 2;
        }
        Mapping m = offsetMap.get("A1");
        int value;
        if (m.isComplement) {
            value = m.mappedIndex - (6 - m.offset);
        } else {
            value = m.mappedIndex + (6 - m.offset);
        }
        System.out.println("Mapped: " + value);
    }

    private static class Mapping {

        int mappedIndex;
        int offset;
        boolean isComplement;

        public Mapping() {
        }

        public Mapping(int mappedIndex, int offset, boolean isComplement) {
            this.mappedIndex = mappedIndex;
            this.offset = offset;
            this.isComplement = isComplement;
        }
    }

    private List<Position> buildList(Group group) {
        List<Position> list = new LinkedList<Position>();
        Position position = group.getPosition();
        if (position != null) {
            list.add(position);
        }
        for (Group g : group.getChildren()) {
            list.addAll(buildList(g));
        }
        return list;
    }

    private int buildList2(Group group, MapValue mv, int seqIndex) {
        MapValue childmv = new MapValue();
        for (Group child : group.getChildren()) {
           seqIndex = buildList2(child, childmv, seqIndex);
        }
        if(group.getPosition() != null){
            mv.positionList.add(group.getPosition());
            if(group.getPosition().getAccession() != null){
                Mapping mapping = new Mapping(seqIndex, group.getPosition().getStartPosition(), false);
                childmv.map.put(group.getPosition().getAccession(), mapping);
            }
            //mv.seqValue = mv.seqValue + (group.getPosition().getEndPosition() - group.getPosition().getStartPosition() + 1);
            //childmv.seqValue = childmv.seqValue + (group.getPosition().getEndPosition() - group.getPosition().getStartPosition() + 1);
            seqIndex = seqIndex + (group.getPosition().getEndPosition() - group.getPosition().getStartPosition() + 1);
        }
        if(group.getOperator() != null && group.getOperator().equals("complement")){
            int length = seqIndex - 1;
            for (Mapping mapping : childmv.map.values()) {
                mapping.mappedIndex = length - mapping.mappedIndex + 1;
                mapping.isComplement = !mapping.isComplement;
            }
        }
        mv.map.putAll(childmv.map);
        
        return seqIndex;
    }

    private static class MapValue {

        List<Position> positionList = new LinkedList();
        HashMap<String, Mapping> map = new HashMap<String, Mapping>();
        int seqValue = 1;
    }

    @Test
    public void lookupAccessionToPosition() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
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
            System.out.println(expression);
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

    public StringBuilder groupAsString(Group group) {
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
            bldr.append(groupAsString(group.getChildren().get(i)));
        }

        if (group.getOperator() != null) {
            bldr.append(")");
        }

        return bldr;
    }

    private static class Group {

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

    public void parseLocation(String locString) throws IOException {
        List<Integer> startParenList = new LinkedList<Integer>();
        List<Integer> endParenList = new LinkedList<Integer>();
        for (int index = locString.indexOf('('); index >= 0; index = locString.indexOf('(', index + 1)) {
            startParenList.add(index);
        }
        for (int index = locString.indexOf(')'); index >= 0; index = locString.indexOf(')', index + 1)) {
            endParenList.add(index);
        }
        if (startParenList.size() != endParenList.size()) {
            throw new IOException("Parentheses invalid.");
        }
        for (int i = 0; i < startParenList.size(); i++) {
            parseSubLocation(locString, startParenList.get(i), endParenList.get((endParenList.size() - 1) - i));
        }
    }

    private EquationGroup parseSubLocation(String locString, int start, int end) {
        EquationGroup group = new EquationGroup();
        Pattern pattern = Pattern.compile("[a-z]+$");
        Matcher m = pattern.matcher(locString.substring(0, start));
        int qualifierStart = 0;
        if (m.find()) {
            qualifierStart = m.start();
        }
        String qualifier = locString.substring(qualifierStart, start);
        String inside = locString.substring(start + 1, end);
        System.out.println(qualifier + "\t" + inside);
        return group;
    }

    private static class EquationGroup {

        String string;
        String qualifier;
        List<EquationGroup> children;
    }
}