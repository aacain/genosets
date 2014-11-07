/*
 * 
 * 
 */

package edu.uncc.genosets.studyset;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author aacain
 */
public class ConditionTree implements Serializable{
    public static final String OPER_AND = "OPER_AND";
    public static final String OPER_OR = "OPER_OR";
    public static final String OPER_NOT_AND = "OPER_NOT_AND";
    public static final String OPER_NOT_OR = "OPER_NOT_OR";
    public static final String OPER_XOR = "XOR";



    private String operator;
    private List<Condition> conditions;
    private List<ConditionTree> children;
    private ConditionTree parent;

    public List<ConditionTree> getChildren() {
        return children;
    }

    public void setChildren(List<ConditionTree> children) {
        this.children = children;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public ConditionTree getParent() {
        return parent;
    }

    public void setParent(ConditionTree parent) {
        this.parent = parent;
    }

    @Override
    public String toString(){
        LinkedList<StringBuilder> bldrList = new LinkedList<StringBuilder>();
        List<Condition> myConditions = this.getConditions();
        if(myConditions != null){
            for (Condition condition : myConditions) {
                getChildConditions(bldrList, condition);
            }
        }

        StringBuilder myString = new StringBuilder();
        for (int i = 0; i < bldrList.size(); i++) {
            StringBuilder b = bldrList.get(i);
            if(i != 0){
                myString.append(" ").append(getBetweenOperator(this.getOperator())).append(" ");
            }
            myString.append(b);
        }
        return myString.toString();
    }

    private void getChildConditions(LinkedList<StringBuilder> bldrList, Condition condition){
        StringBuilder bldr = new StringBuilder();
        bldrList.add(bldr);
        bldr.append(condition.getDisplayName()).append(" ").append(condition.getOperator()).append(" ").append(condition.getValue());
    }

    public static String getBetweenOperator(String treeOperator){
        if((OPER_AND).equals(treeOperator) || (OPER_NOT_AND).equals(treeOperator)){
            return "AND";
        }else{
            return "OR";
        }
    }
}
