import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    Socket socket=null;
//    PrintWriter pw=null;
//    BufferedReader bfr=null;
    ObjectOutputStream oos=null;
    ObjectInputStream ois=null;
    public static void main(String[] args) {
        Client client=new Client();
        View view=new View(client);

    }
    public boolean connectToServer(String domainName, String inputPortNumber) {
        try {
            int portNumber = Integer.parseInt(inputPortNumber);
            socket = new Socket(domainName, portNumber);
            if(socket.isConnected()){
//                pw=new PrintWriter(socket.getOutputStream());
//                bfr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                oos=new ObjectOutputStream(socket.getOutputStream());
                ois=new ObjectInputStream(socket.getInputStream());
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    public boolean createAccount(String username, String password, String typeOfAccount){
//        pw.println("create-account "+username+" "+ password + " "+typeOfAccount);
//        pw.flush();
        try {
            ArrayList<Object> objects=new ArrayList<>();
            objects.add("create-account");
            objects.add(username);
            objects.add(password);
            objects.add(typeOfAccount);

            oos.writeObject(objects);
            oos.flush();
            String checker =(String)((ArrayList<Object>) ois.readObject()).get(0);
            if(checker.equals("success")){
                return true;
            }
        } catch (Exception e){
            throw new RuntimeException();
        }
        return false;
    }
    public boolean login(String username, String password, String typeOfAccount){
//        pw.println("login "+username+" "+password+" "+ typeOfAccount);
//        pw.flush();
        try {
            ArrayList<Object> objects=new ArrayList<>();
            objects.add("login");
            objects.add(username);
            objects.add(password);
            objects.add(typeOfAccount);
            oos.writeObject(objects);
            oos.flush();
            String checker =(String)((ArrayList<Object>) ois.readObject()).get(0);
            if(checker.equals("success")){
                return true;
            }
        } catch (Exception e){
            throw new RuntimeException();
        }
        return false;

    }
    public ArrayList<Course> getAccountCourses(){
//        pw.println("get-courses");
//        pw.flush();
        try{
            ArrayList<Object> objects=new ArrayList<>();
            objects.add("get-courses");
            oos.writeObject(objects);
            oos.flush();
            ArrayList<Course> courseList=(ArrayList<Course>) ois.readObject();
            return courseList;
        } catch(Exception e){
            throw new RuntimeException();
        }
    }

    public boolean createCourse(String courseName, int courseNumber) {
//        pw.println("create-course "+courseName+" "+courseNumber);
//        pw.flush();
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("create-course");
            objects.add(courseName);
            objects.add(courseNumber);
            oos.writeObject(objects);
            oos.flush();
            String checker =(String)ois.readObject();
            if(checker.equals("success")){
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return false;
    }

//    public void deleteQuiz() {
//
//    }
//
    public boolean addImportedQuiz(File f) {
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("import-quiz-file");
            objects.add(f);
            oos.writeObject(objects);
            oos.flush();
            String checker = (String) ois.readObject();
            if(checker.equals("success")){
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return false;
    }

    public boolean createQuiz(Quiz quiz) {
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("create-quiz");
            objects.add(quiz);
            oos.writeObject(objects);
            oos.flush();
            String checker =(String)ois.readObject();
            if(checker.equals("success")){
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return false;
    }
    public void addQuestionToQuiz(Question question) {
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("add-question-to-quiz");
            objects.add(question);
            oos.writeObject(objects);
            oos.flush();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public ArrayList<Question> getQuestions() {
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("get-questions");
            oos.writeObject(objects);
            oos.flush();
            ArrayList<Question> questionList = (ArrayList<Question>) ois.readObject();
            return questionList;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void setActiveQuestion(int questionNumber) {
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("set-active-question");
            objects.add(questionNumber);
            oos.writeObject(objects);
            oos.flush();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

//    public boolean deleteQuestion() {
//
//    }
//
    public void updateQuestion(Question question) {
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("update-question");
            objects.add(question);
            oos.writeObject(objects);
            oos.flush();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void submitSubmission(Submission submission) {
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("submit-submission");
            objects.add(submission);
            oos.writeObject(objects);
            oos.flush();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public ArrayList<Submission> getAllSubmissions() {
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("get-all-submissions");
            oos.writeObject(objects);
            oos.flush();
            ArrayList<Submission> submissionList = (ArrayList<Submission>) ois.readObject();
            return submissionList;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void setActiveSubmission(int submissionNumber) {
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("set-active-submission");
            objects.add(submissionNumber);
            oos.writeObject(objects);
            oos.flush();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
//
    public Quiz getCurrentQuiz() {
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("get-current-quiz");
            oos.writeObject(objects);
            oos.flush();
            Quiz currentQuiz = (Quiz) ois.readObject();
            return currentQuiz;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
//
//    public void clearActiveSubmissions() {
//
//    }
//
    public void setActiveCourse(int courseChosen) {
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("set-active-course");
            objects.add(courseChosen);
            oos.writeObject(objects);
            oos.flush();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
//
//    public void close() {
//
//    }
//
    public ArrayList<Course> getAllCourses() {
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("get-all-courses");
            oos.writeObject(objects);
            oos.flush();
            ArrayList<Course> courseList = (ArrayList<Course>) ois.readObject();
            return courseList;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public ArrayList<Quiz> getQuizzes() {
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("get-quizzes");
            oos.writeObject(objects);
            oos.flush();
            ArrayList<Quiz> quizList = (ArrayList<Quiz>) ois.readObject();
            return quizList;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void setActiveQuiz(int quizNumber) {
        try {
            ArrayList<Object> objects = new ArrayList<>();
            objects.add("set-active-quiz");
            objects.add(quizNumber);
            oos.writeObject(objects);
            oos.flush();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
//
//    public void clearActiveCourse() {
//
//    }
//
//    public void clearActiveSubmission() {
//
//    }




}
