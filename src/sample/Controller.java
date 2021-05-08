package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    
    @FXML
    TextField mainfield,powerfield,multifield;//Fields for inputs (Expression, power , and term extractor from coeffs powers)
    @FXML
    ComboBox<String> combo;//combo box for different modes of expansions
    @FXML
    Label type;//Instruction and Error screen
    @FXML
    HBox hbox;//Output box

    //variable for storing multinomial expression
    String expression;
    //variable for storing multinomial expansion term's powers from multifield for custom coefficient
    String extractor_field =null;
    
    //counter for error codes in expression and second counter for generating largest coefficient(s)
    int counter=0;int modecounter=0;
    
    //variables for generating all possible combinations of powers of multinomial coefficients
    int p1 =0, p2 =0, p3 =0, p4 =0,p5=0,p6=0;
    
    //for storing largest coefficient
    long largest;
    
    //for storing every term's coefficients powers
    int[] coeffextract={0,0,0,0,0,0};
    
    //variable for checking every term's powers with custom multifield powers (user input)
    boolean coeffcheck=false;

    //dynamic variables for storing operators indexes,operators,multinomial variables coefficients and multinomial variables
    ArrayList<Integer> operatorsIndex = new ArrayList<>();
    ArrayList<String> operators=new ArrayList<>();
    ArrayList<Integer> coeffs = new ArrayList<>();
    ArrayList<String> variables = new ArrayList<>();

    /**============================================== METHOD FOR SWITCHING OUTPUT MODES =============================================**/
    public void setCombo() {
        //for outputting full expression
        switch (combo.getValue()) {
            case "Full Expression" -> {
                //no use of multifield
                multifield.setDisable(true);
                
                //green message box
                type.setTextFill(Color.GREEN);
                type.setText("Multinomial Expression -> eg- aX1+bX2+cX3+....+");
                
                //for only printing the result
                modecounter = 1;
            }
            //for outputting largest coefficient
            case "Largest Coefficient" -> {
                //no use of multifield
                multifield.setDisable(true);
                
                //green message box
                type.setTextFill(Color.GREEN);
                type.setText("Largest Coefficient->");
                
                //for firstly storing largest coefficient then printing final result
                modecounter = 2;
            }
            //for outputting custom terms based on variables powers
                case "Coefficient from Powers" -> {
                //no use of multifield
                multifield.setDisable(false);
    
                //green message box
                type.setTextFill(Color.GREEN);
                type.setText("Extract Particular Coefficient terms by giving their (comma separated) Variable(s) Powers Eg-2,1,...");
    
                //for only printing the result
                modecounter = 1;
            }
        }
    }
    
    /**======================================= METHOD FOR DETERMINING THE TYPE OF MULTINOMIAL =====================================**/
    public long mode(){
        //checking for output mode
        switch (combo.getValue()) {
            //for Full Expression
            case "Full Expression" -> {
                //Type of multinomial is printed at the message box
                switch (variables.size()) {
                    case 1 -> type.setText("Monomial Expansion");
                    case 2 -> type.setText("Binomial Expansion");
                    case 3 -> type.setText("Trinomial Expansion");
                    case 4 -> type.setText("Quadnomial Expansion");
                    case 5 -> type.setText("Pentanomial Expansion");
                    case 6 -> type.setText("Hexanomial Expansion");
                }
                //for telling its calling place that 1st mode is selected
                return -1;
            }
            //for Largest Coefficient mode
            case "Largest Coefficient" -> {
                //Message box
                type.setText("Largest Coefficient Terms ->>>");
                //for telling its calling place that 2nd mode is selected
                return -2;
            }
            //for custom coefficient mode
            case "Coefficient from Powers" -> {
                //extracting the powers of variables in array format from raw text format
                coeffextract = termExtractor();
                //for telling its calling place that 3rd mode is selected
                return -3;
            }
        }
        //useless return but necessary for resolving syntax error
        return 0;
    }
    
    /**================================= METHOD FOR CUTTING MULTINOMIAL EXPRESSION INTO DIFF PARTS =============================**/
    public void Expression_cutter(){
        //resetting counter for new input
        counter=0;

        //expression should not be empty
        if(!mainfield.getText().equals("")){
            expression=mainfield.getText();
            
            //adding '+' at the beginning of the expression if no operator is present at there
            if(expression.charAt(0) != '+' && expression.charAt(0) != '-')expression="+"+expression;

            //loop for iterating and storing indexes of operators
            for(int i=0;i<expression.length()-1;i++)
                if(expression.charAt(i)=='+' || expression.charAt(i)=='-')
                    operatorsIndex.add(i);

                //loop for iterating and storing trimmed expression between two operators (has operator , coefficient and single variable)
            for(int i = 0; i< operatorsIndex.size()-1; i++)
                variables.add(expression.substring(operatorsIndex.get(i), operatorsIndex.get(i+1)));

            //error handling for checking multiple operator b/w two alternate variable
            try{
                //adding final variable to the variable list which is missed from above loop
                variables.add(expression.substring(operatorsIndex.get(operatorsIndex.size()-1)));
            }catch(Exception e){
                //created error code 2 for above error
                counter=2;
            }

            //loop for iterating and extracting operators from variables list
            for(int i = 0; i< variables.size(); i++){
                operators.add(variables.get(i).substring(0,1));

                //simultaneously extracting variable coefficients from variable list if each item length is greater than 2 , it must be the coefficient
                if(variables.get(i).length()>2){
                    
                    //also variable coefficients should be integers
                    try{
                        //extracting variables coefficients
                        coeffs.add(Integer.valueOf(variables.get(i).substring(1,variables.get(i).length()-1)));
                    }catch(Exception e){
                        //created error code 2 for above error
                        counter=2;
                    }
                }
                //also when no coefficient is entered then that variable's coefficient is 1
                else coeffs.add(1);

                //here i want a error everytime , for checking that integer should not be stored inside the variable list
                try{
                    //converting string into integer
                    Integer.parseInt(variables.get(i).substring(variables.get(i).length()-1));
                    //created error code 2 if it do so
                    counter=2;
                }catch(Exception e){
                    //if this is string then error occur , which is i wanted to store string in variable list
                    variables.set(i,variables.get(i).substring(variables.get(i).length()-1));
                }

            }
        }
        //created error code 1 for empty field
        else counter=1;

        //cleared all the variables for new input
        p1 = p2 = p3 = p4 = p5 = p6 = 0;
        hbox.getChildren().clear();

        //loop for final inspection of all the lists that the expression is valid (is in standard form) or not
        for(int i=0;i<variables.size();i++){
            
            //if error codes are already present then no need for checking further errors, it will terminate the output
            if(counter==1 || counter==2)break;
            
            //All variables Symbols should be b/w 'a to z' or 'A to Z'
            //variable lists should not contain any operator , size of all the lists should be equal , and should be less than 6
            else if(!(((int)variables.get(i).charAt(0) >=97 && (int)variables.get(i).charAt(0) <=102) ||
                         ((int)variables.get(i).charAt(0) >=65 && (int)variables.get(i).charAt(0) <=91  )) ||
                        variables.get(i).equals("+") || variables.get(i).equals("-") ||
                        variables.size()!=coeffs.size() || coeffs.size()!=operators.size() || variables.size()>6) {
                
                //creating error code 2 for above errors
                counter=2;break;
            }
            //if everything i alright then we are good to go
            else counter=0;
        }

        //if above errors are passed then power checking is initiated
        if(counter==0)
        //error handling for checking power field input
        try{
            //field should not be empty and must be a positive integer
            if(!powerfield.getText().equals("") && Integer.parseInt(powerfield.getText())<0)counter=3;
            
            //power should be less than 21 or less depending on no. of multinomial variables (cause limitation of factorial calculation up to 20)
            if(Integer.parseInt(powerfield.getText())>(20-coeffs.size()+1) && counter!=2)counter=3;
        }catch (Exception e){
            //otherwise error code 3 for anything wrong with the power input
                counter=3;
        }
        
        /*THE FOUR HORSEMAN OF DEBUGGING  :|
        System.out.println(operators);
        System.out.println(coeffs);
        System.out.println(variables);
        System.out.println(counter);*/

        //error 1 if expression field is empty
        if(counter==1){
            type.setTextFill(Color.DARKCYAN);
            type.setText("Please Enter a Expression");
        }
        //error code 2 if the expression is not in the standard form
        else if(counter==2){
            type.setTextFill(Color.RED);
            type.setText("INVALID EXPRESSION");
            type.setText(type.getText()+"\nExpression Should be of the form (aX1+bX2+cX3+....+) up to six terms ");
        }
        //error when powerfield is empty
        else if(powerfield.getText().equals("")){
            type.setTextFill(Color.DARKCYAN);
            type.setText("Please Enter Power");
        }
        //error code 3 if there is anything wrong in the input power
        else if(counter==3){
            type.setTextFill(Color.RED);
            type.setText("INVALID POWER");
            type.setText(type.getText()+"\nPower should be a Positive Integer and  and for This Expression should be less than " +(20-coeffs.size()+1) +
                    " because of your PC limitations");
        }
        //also i power is 0 then ans is 1 for any expression and program gets terminated
        else if(Integer.parseInt(powerfield.getText())==0){
            
            //Dynamic label for printing 1
            Label coefflabel=new Label();
            coefflabel.setText("1");
            coefflabel.setFont(Font.font(null,FontWeight.NORMAL,15));
            
            //calling mode function for determining the type of multinomial
            mode();
            hbox.getChildren().addAll(coefflabel);
        }
        //Otherwise ready for the complex output if all the error checks are passed
        else if(counter==0){
            type.setTextFill(Color.GREEN);
            
            //getting output mode
            setCombo();
            //resetting largest for new input
            largest=0;
            
            //and here we go
            term_builder(Integer.parseInt(powerfield.getText()),coeffs.size());
        }

        //clearing all the lists after their use
        operatorsIndex.clear();
        operators.clear();
        coeffs.clear();
        variables.clear();
    }
    
    /**============================= METHOD FOR CALCULATING LARGEST COEFFICIENT FROM WHOLE EXPANSION =========================**/
    public void largestcoeff(int j, int k, int l, int m, int n, int o, int power){
        //variable for storing current coefficient
        long coefficient;
        //for storing current powers of each term's multinomial variables (from arguments)
        int[] coeffpowers={j,k,l,m,n,o};

        //calculating multinomial coefficient n!/(a!*b!*c!*....)
        coefficient=(factorial(power)/(factorial(j)*factorial(k)*factorial(l)*factorial(m)*factorial(n)*factorial(o)));

        //multiplying additional factors generated by multinomial variables powers of each term, if they have some some coefficient attached at the input
        for (int i = 0; i < coeffs.size(); i++) {
            coefficient = (long) (coefficient * Math.pow(coeffs.get(i), coeffpowers[i]));
        }

        //and storing largest among them
        if(largest<coefficient)
            largest=coefficient;
    }
    
    /**================== METHOD FOR EXTRACTING CUSTOM COEFFICIENT POWERS FROM RAW TEXT INPUT TO INT ARRAY ===============**/
    public int[] termExtractor(){
        //resetting iterator and sum for new input
        int i=0,sum=0;

        //multifield should not be empty
        if(!multifield.getText().isEmpty()){
            //appending ',' at the end of the expression
            extractor_field =multifield.getText()+",";
            
            //error handling for more than one commas between adjacent inputs
            try{
                //iterating, cutting , and storing expression until it becomes empty
                    while (!extractor_field.equals("")){
                        
                        //cutting the expression until first comma is found
                        coeffextract[i]=Integer.parseInt(extractor_field.substring(0, extractor_field.indexOf(',')));
                        
                        //and storing rest of the expression overwriting the same variable
                        extractor_field = extractor_field.substring(extractor_field.indexOf(',')+1);
                        
                        //storing their sum for further error handling or for instruction to noobs
                        sum+=coeffextract[i];
                        //same for their counts as well
                        i++;
                    }

                    //input count should be = multinomial variables
                if(i==coeffs.size()){
                    //and their sum must also be = whole expression's power
                    if(sum==Integer.parseInt(powerfield.getText())){
                        type.setTextFill(Color.GREEN);
                        type.setText("Your required Coefficient term is");
                    }
                    //error message corresponding to wrong input
                    else  {
                        type.setTextFill(Color.RED);
                        type.setText("No terms exist corresponding to your input  (their sum must be equal to Expression Power)");
                        
                        //returning the value that should not output anything on the output screen
                        return new int[]{-1,0,0,0,0,0};
                    }
                }
                //error message corresponding to wrong input
                else if(i<coeffs.size()){
                    type.setTextFill(Color.RED);
                    type.setText("Incomplete Input (Complete it by putting 0)");
    
                    //returning the value that should not output anything on the output screen
                    return new int[]{-1,0,0,0,0,0};
                }
                //and obviously input count should not be greater than multinomial variables
                else {
                    type.setTextFill(Color.RED);
                    type.setText("Input Exceeded (No. of inputs must be equal to Number of multinomial term) ");
    
                    //returning the value that should not output anything on the output screen
                    return new int[]{-1,0,0,0,0,0};
                }
            }
            //and catching error by displaying some message
            catch(Exception e){
                //e.printStackTrace();
                type.setTextFill(Color.RED);
                type.setText("Invalid Input");
                
                //also returning the value that should not output anything on the output screen
                return new int[]{-1,0,0,0,0,0};
            }
        }
        //Field is empty
        else {
            type.setTextFill(Color.RED);
            type.setText("Field is Empty...");
        }
        
        //till here if everything is correct then raw text input is converted into int array
        return coeffextract;
    }

    /**=================  METHOD FOR GENERATING ALL POSSIBLE PERMUTATIONS OF POWERS FOR MULTINOMIAL EXPANSION ==========**/
    public void term_builder(int n, int r){
        //resetting counter for new input
        counter=0;

        
        /*  if mode is selected to largest coefficient then modecounter=2
          *  external loop for firstly calculating largest coefficient from inner loop (whole expression must be traversed for that)
          *  then again inner loop run but this time, creating output to display corresponding to the largest coefficient
          * */
            while(--modecounter>=0){//modecounter=1
                
                //resetting variables for new iteration for expansion building
                p1=p2=p3=p4=p5=p6=0;
                
                //inner loop generating all types of combinations of powers of every term of multinomial expansion
                //and no. of terms in multinomial expansion = (n+r-1)C(r-1)
                for (int i = 1; i <= nCr(n + r - 1, r - 1); i++) {
                    
                    //sum of all the powers of all the multinomial variables must be = whole expression's power
                    if (p1 + p2 + p3 + p4 + p5 + p6 == n) {
                        
                        //firstly largest coefficient gets built
                        if (modecounter == 1)
                            largestcoeff(p1, p2, p3, p4, p5, p6, n);
                        
                        //then multinomial expansion
                         else if (modecounter == 0)
                            multinomialPrinter(p1, p2, p3, p4, p5, p6, n, i);
                        
                    }
                    //loop will continue without changing its iterator if above condition doesn't pass , generating all the combinations
                    else i--;
    
                    /*incrementing very last power variable such that it does not exceed the multinomial power
                    * if that power variable reaches multinomial power then its previous variable incremented by 1
                    * this process will continue until the first power variable will reach multinomial power
                    * at that point all possible combinations are covered
                    * Further , no. of power variables to consider for this process will vary acc to multinomial variables
                    * rest of the variables will be ignored depending on r = multinomial variables
                    * */
                    if (p1 == n && r >= 2) {
                        p1 = 0;
                        if (p2 == n && r >= 3) {
                            p2 = 0;
                            if (p3 == n && r >= 4) {
                                p3 = 0;
                                if (p4 == n && r >= 5) {
                                    p4 = 0;
                                    if (p5 == n && r >= 6) {
                                        p5 = 0;
                                        p6++;
                                    } else p5++;
                                } else p4++;
                            } else p3++;
                        } else p2++;
                    } else p1++;
                }//inner loop's end
            }//outer loop's end
    }

    /**===================================== METHOD FOR PRINTING MULTINOMIAL EXPANSION ===========================================**/
    public void multinomialPrinter(int j,int k,int l,int m,int n,int o,int power,int iterator){
        //variable for checking  coefficient from powers input = current term's variables powers
        coeffcheck=true;
        //variable to store current coefficient
        long coefficient;
        //array to store current term's variables powers
        int[] coeffpowers={j,k,l,m,n,o};
        //variable for determining the sign of each term
        int sign=1;

        //Dynamic Label list for storing multinomial variables for printing on the screen
        ArrayList<Label> variableLabel=new ArrayList<>();
        
        //calculating coefficient by traditional way of multinomial coefficient
        coefficient=(factorial(power)/(factorial(j)*factorial(k)*factorial(l)*factorial(m)*factorial(n)*factorial(o)));
    
        //multiplying additional factors generated by multinomial variables powers of each term, if they have some some coefficient attached at the input
        for (int i = 0; i < coeffs.size(); i++) {
            //corresponding variable coefficient raise to its corresponding power
            coefficient = (long) (coefficient * Math.pow(coeffs.get(i), coeffpowers[i]));
        }

        //FULL EXPRESSION is selected
        if(mode()==-1){
            //largest is bypassed by overwriting itself in each iteration making the whole output printable
            largest=coefficient;
            //also coeffcheck is bypassed
            coeffcheck=true;
        }
        // LARGEST COEFFICIENT is selected
        else if(mode()==-2){
            //only coeffcheck gets bypassed but largest is being brought out from its function
          coeffcheck=true;
        }
        // COEFFICIENT FROM POWERS is selected
        else if(mode()==-3){
            //only largest is bypassed but coeffcheck will happen
            largest=coefficient;

            //loop for checking every index of extracted array from multifield to current coefficient powers
            for(int i=0;i<6;i++){
                if (coeffpowers[i] != coeffextract[i]) {
                    coeffcheck = false;
                    break;
                }
            }//if everything is alright then coeffcheck will not change until the final iteration and we get our desired term
        }

        //output based on largest coefficient and coeffcheck
        if(largest==coefficient && coeffcheck) {
            
            //loop for iterating every variable in current term
            for (int i = 0; i < operators.size(); i++)
                //altering sign only for -ve coefficients which has odd powers
                if (operators.get(i).equals("-") && coeffpowers[i] % 2 == 1)
                    sign *= -1;

                // sign should not be printed before first term unless it is -ve sign
            if (iterator == 1 && sign < 0) {
    
                //Dynamic Label for sign of every term
                Label operatorLabel = new Label("-");
                hbox.getChildren().addAll(operatorLabel);
                
            }//also first term to be printed should not contain any sign with it (counter = 0 for first term)
            else if (iterator != 1 && counter!=0) {
                
                //Dynamic Label for sign of every term
                Label operatorLabel = new Label();

                if (sign > 0)
                    operatorLabel.setText(" + ");
                else
                    operatorLabel.setText(" - ");

                hbox.getChildren().addAll(operatorLabel);
                
            }
            //changed for further terms
            counter=1;
            
            //coefficient printed of every term should not be 0 and 1
            if (coefficient != 1 && coefficient != 0) {
                
                //Dynamic Label for coefficient of every term
                Label coefflabel = new Label();
                coefflabel.setText(String.valueOf(coefficient));
                coefflabel.setFont(Font.font(null, FontWeight.NORMAL, 15));
                hbox.getChildren().addAll(coefflabel);
            }

            //loop for iterating every multinomial variable of the current term
            for (int i = 0; i < variables.size(); i++) {
                //they should not be printed if they have power 0
                if (coeffpowers[i] != 0) {
                    
                    //Dynamic label for multinomial's current variable of iteration
                    Label label1 = new Label();
                    label1.setText(variables.get(i));
                    label1.setFont(Font.font(null, FontWeight.NORMAL, 15));
                    
                    //adding this to Dynamic Label list
                    variableLabel.add(label1);
                   
                }
                
                //also 0 and 1 power should not be printed
                if (coeffpowers[i] != 1 && coeffpowers[i] != 0) {
                    
                    //Dynamic Label for multinomial current variable's power
                    Label label2 = new Label();
                    label2.setText(String.valueOf(coeffpowers[i]));
                    label2.setFont(Font.font(null, FontWeight.NORMAL, 10));
                    
                    //adding this to Dynamic Label list
                    variableLabel.add(label2);
                }
            }
            // and here completes the building of one term of multinomial Expansion, This process is repeated until the whole expression is printed on the Screen
            
            //adding Dynamic variable's list
            hbox.getChildren().addAll(variableLabel);
            
            //and clearing that list after its use
            variableLabel.clear();
        }
    }

    /**============================================ METHOD FOR GENERATING FACTORIAL ===================================================**/
    public  long factorial(int num){
        long f=1;
        for(int i=1;i<=num;i++)
            f=f*i;
        if(num>=1)
            return f;
        else
            return 1;
    }
    
    /**================================================== METHOD FOR GENERATING nCr ======================================================**/
    public long nCr(int n,int r){
        return factorial(n)/(factorial(r)*factorial(n-r));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        //adding elements in the mode combo box
        combo.getItems().addAll("Full Expression","Largest Coefficient","Coefficient from Powers");
        //and selecting its initial value
        combo.setValue("Full Expression");

        //disabling multifield by default
        multifield.setDisable(true);
        //and analysing the selection mode just for printing expression input instructions
        setCombo();
    }
}
