public class Question {
    String question;
    int pointValue;
    public Question(String question, int pointValue){
        this.question = question;
        this.pointValue = pointValue;
    }
    public String getQuestion() {
        return question;
    }

    public int getPointValue() {
        return pointValue;
    }


    public void setQuestion(String question) {
        this.question = question;
    }

    public void setPointValue(int pointValue) {
        this.pointValue = pointValue;
    }

}
