import Common.CustomException;

import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

public class Calculator {
    static Stack<String> stack = new Stack<String>();//当前栈数据
    static Stack<Stack<String>> stackHistory=new Stack<Stack<String>>();//栈的历史记录
    public static void main(String[] args){
        Calculator calculator = new Calculator();
        System.out.println("如果退出计算则输入quit");
        Scanner scn = new Scanner(System.in);

        while (scn.hasNextLine()) {
            try {
                String inStr = scn.nextLine().trim();
                if ("quit".equals(inStr)) {
                    System.out.println("退出计算");
                    break;
                }
                if (!"".equals(inStr)) {
                    String[] inStrArray = inStr.split(" ");
                    //如果输入的有不合格的字符
                    if (!calculator.vaildInStr(inStrArray)) {
                        System.out.println("请输入有效的数字或者操作符(sqrt、undo、clear)");
                        continue;
                    }
                    //将输入的推入栈中
                    calculator.doCalculator(inStrArray);
                    System.out.println("stack:"+ calculator.getStrStack());
                }
            }catch (CustomException e){
                System.out.println(e.getMessage()+":stack:"+ calculator.getStrStack());
            }catch (Exception e){
                System.out.println("出现异常");
            }
        }

    }

    /**
     * 将输入的内容推入栈中，如果有操作符则先进行操作，之后将结果推入栈中
     * @param inStrArray
     */
    private void doCalculator(String[] inStrArray){
        StringBuilder appendInStr=new StringBuilder();
        for(int i=0;i<inStrArray.length;i++){
            String inStr=inStrArray[i];
            if(isTwoOperator(inStr)){
                if(stack.size() <2){//如果操作符操作的是两个元素，则需要判断栈中是否有至少两个可操作的元素
                    throw new CustomException("(position:"+(appendInStr.length()+i+1)+") stack out of ");
                }
                Double oper1=Double.valueOf(stack.pop());
                Double oper2=Double.valueOf(stack.pop());
                Double result=null;
                if("+".equals(inStr)){
                    result=oper1+oper2;
                }else if("-".equals(inStr)){
                    result=oper2-oper1;
                }else if("*".equals(inStr)){
                    result=oper1*oper2;
                }else if("/".equals(inStr)){
                    if(!(Math.abs(oper1) > 0)){
                        throw new CustomException("(position:"+(appendInStr.length()+i+1)+")  divide by zero");
                    }
                    result=oper2/oper1;
                }
                String resultStr=DoubleToStr(result);
                if(resultStr!=null){
                    pushStack(resultStr);
                    //stack.push(inStr);
                    //stackHistory.push(stack);
                }
            }else if(isOneOperator(inStr)){
                if(stack.size() <1){//如果操作符操作的是一个元素，则需要判断栈中是否有至少一个可操作的元素
                    throw new CustomException("stack out of");
                }
                Double oper1=Double.valueOf(stack.pop());
                Double result=null;
                if("sqrt".equals(inStr)){
                    result = Math.sqrt(oper1);
                }

                String resultStr=DoubleToStr(result);
                if(resultStr!=null){
                    pushStack(resultStr);
                    //stack.push(inStr);
                    //stackHistory.push(stack);
                }
            }else if("clear".equals(inStr)){
                stack.clear();
                stackHistory.clear();
                //stackHistory.push((Stack<String>)stack.clone());

            }else if("undo".equals(inStr)){
                if(stackHistory.size()>=1){
                    stackHistory.pop();
                }
                if(stackHistory.size()==0){
                    stack.clear();
                }else{
                    stack=stackHistory.pop();
                }
                stackHistory.push((Stack<String>)stack.clone());
            }else{
                pushStack(inStr);
                //stack.push(inStr);
                //stackHistory.push(stack);
            }
            appendInStr.append(inStr);
        }
    }

    /**
     * 将输入push到栈中，同时将当前栈push到栈历史列表中
     * @param inStr
     */
    private void pushStack(String inStr){
        stack.push(inStr);
        stackHistory.push((Stack<String>)stack.clone());
    }

    /**
     * 校验输入的格式是否正确
     * @param inStrArray
     * @return true：正确  false：错误
     */
    private boolean vaildInStr(String[] inStrArray){
        String regex="^[0-9]{1,}|\\+|-|\\*|/|sqrt|undo|clear$";
        //Pattern pattern = Pattern.compile("^[0-9]{1,}|\\+|-|\\*|/|sqrt|undo|clear$");
        for(String inStr : inStrArray){
            //System.out.println(inStr);
            if(!Pattern.matches(regex,inStr)){
                return false;
            }
        }
        return true;
    }

    /**
     * 判断运算符操作的是否是两个个元素
     * @param str
     * @return
     */
    private boolean isTwoOperator(String str){
        if("+".equals(str) || "-".equals(str) || "*".equals(str) || "/".equals(str)){
            return true;
        }
        return false;
    }

    /**
     * 判断运算符操作的是否是一个元素
     * @param str
     * @return
     */
    private boolean isOneOperator(String str){
        if("sqrt".equals(str)){
            return true;
        }
        return false;
    }

    /**
     * 将double多余的0去掉转为对应格式的string
     * @param db
     * @return
     */
    private String DoubleToStr(Double db){
        if(db==null){
            return null;
        }
        String strDb=db.toString();
        return strDb.replaceAll("0{1,}$|\\.0{1,}","");
    }

    /**
     * 将栈中的数据返回字符
     * @return
     */
    private String getStrStack(){
        Object[] stackArray = new Object[stack.size()];
        stack.copyInto(stackArray);
        String strStack="";
        for(Object o : stackArray){
            strStack+=o.toString()+" ";
        }
        return strStack;
    }

    /*class CustomException extends RuntimeException {
        //无参构造方法
        public CustomException(){
            super();
        }

        //有参的构造方法
        public CustomException(String message){
            super(message);

        }

        // 用指定的详细信息和原因构造一个新的异常
        public CustomException(String message, Throwable cause){
            super(message,cause);
        }

        //用指定原因构造一个新的异常
        public CustomException(Throwable cause) {
            super(cause);
        }
    }*/
}
