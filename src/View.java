import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Scanner;

//Explanations for Client methods:
//I probably haven't explained this well. Please ask me if you need clarification on anything, or if something in this
//plan needs to be edited because it won't work with the rest of the program.
//*
//*
//*
//Program Location (details for almost all methods):
//Client should use fields to track the current program location, which includes the following:
// - the active user
// - the active course
// - the active quiz
// - the active question
// - the active submission
//Not all of these will be necessary at any given time (eg in the main menu, the user has not selected a course,
//so there is no active course).
//If a particular component is inactive, it should be set to null.
//I have included a few clear methods that will indicate to client when one of the active objects is being exited and
//should be set to null.
//However, other methods (eg deleteQuiz()) will also need to clear the active object.
//(Implementation details can be different from this; I just need the Client to know what to access at any given time.)
//Any getter methods should take the program location into account and get only the lists from that particular location.
//(eg if a particular quiz is active, the Client should get the questions from that particular quiz.)
//Getter methods use numbers because I can't transfer objects from the screen creator to the action listener.
//If absolutely necessary, I can try to determine how to do this, but it will likely take several hours of work.
//*
//*
//*
//Difference between getAccountCourses() and getAllCourses():
//getAccountCourses() is used to access the courses an account already has - that is, that account's ArrayList of courses
//getAllCourses() is used to let students add courses, so it should get every course that has been created and allow user selection
//*
//*
//*
//What close() does:
//close() indicates that the user has quit the program, and that the Client should ensure any remaining data is stored,
//then also terminate.
//*
//*
//*
//Again, if I haven't explained this well or have planned something that won't work, please let me know so we can
//discuss further.

//TODO: implement the rest of the menu logic
//parts of the menu logic remaining:
//creating courses - should allow teacher to create a course, then advance to course menu
//adding courses - should allow student to add any course, then advance to course menu
//course menu - should allow access to any quiz (and adding quiz if teacher), as well as exiting
//quiz options menu - should allow choice of options for quizzes (different between teacher and student)
//create quiz - long series of methods allowing teacher to create quiz
//edit quiz - displays list of questions, then allows teacher to edit any particular question
//take quiz - long series of methods allowing student to take quiz
//view submission - displays submissions to quiz, then allows viewing of any submission
//TODO: make sure client gets notified when view closes! (might be better implemented in client)

/**
 * Online Quiz Navigator v2 - View
 *
 * Handles the GUI for the Online Quiz Navigator.
 * Listens for user updates, then sends them to Client for handling.
 * Listens for updates from Client, then refreshes the board as need be.
 *
 * @author Nathan Reed, lab sec L24
 * @version November 29, 2021
 */
public class View extends JComponent {
    private Client client;
    private JFrame frame;
    private JPanel mainPanel;
    private ArrayList<Object> activeComponents = new ArrayList<Object>();

    private int accountType;
    private static final int STUDENT_OPTION = 0;
    private static final int TEACHER_OPTION = 1;

    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
            if (actionCommand.equals("send connection info")) {
                JTextField domainNameTxt = (JTextField) activeComponents.get(0);
                JTextField portNumberTxt = (JTextField) activeComponents.get(1);
                if (client.connectToServer(domainNameTxt.getText(), portNumberTxt.getText())) {
                    createLoginScreen();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Unable to connect to the server with the given domain name and " +
                            "port number. Please try again.", "Unable to Connect", JOptionPane.ERROR_MESSAGE);
                }
            } else if (actionCommand.equals("send login info")) {
                JTextField usernameTxt = (JTextField) activeComponents.get(0);
                JTextField passwordTxt = (JTextField) activeComponents.get(1);
                ButtonGroup studentOrTeacher = (ButtonGroup) activeComponents.get(2);
                if (studentOrTeacher.getSelection().getActionCommand().equals("student")) {
                    accountType = STUDENT_OPTION;
                } else if (studentOrTeacher.getSelection().getActionCommand().equals("teacher")) {
                    accountType = TEACHER_OPTION;
                }
                if (client.login(usernameTxt.getText(), passwordTxt.getText(),
                        studentOrTeacher.getSelection().getActionCommand())) {
                    createMainMenu();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Unable to log in to the account with the given username or password. " +
                            "Please try again.", "Unable to login", JOptionPane.ERROR_MESSAGE);
                }
            } else if (actionCommand.equals("send account creation info")) {
                JTextField usernameTxt = (JTextField) activeComponents.get(0);
                JTextField passwordTxt = (JTextField) activeComponents.get(1);
                ButtonGroup studentOrTeacher = (ButtonGroup) activeComponents.get(2);
                if (studentOrTeacher.getSelection().getActionCommand().equals("student")) {
                    accountType = STUDENT_OPTION;
                } else if (studentOrTeacher.getSelection().getActionCommand().equals("teacher")) {
                    accountType = TEACHER_OPTION;
                }
                if (client.createAccount(usernameTxt.getText(), passwordTxt.getText(),
                        studentOrTeacher.getSelection().getActionCommand())) {
                    createMainMenu();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "The account with the given username already exists. " +
                                    "Please try again.", "Unable to create account", JOptionPane.ERROR_MESSAGE);
                }
            } else if (actionCommand.equals("choose course")) {
                ButtonGroup courseList = (ButtonGroup) activeComponents.get(0);
                String courseChosen = courseList.getSelection().getActionCommand();
                if (courseChosen.equals("add course")) {
                    if (accountType == STUDENT_OPTION) {
                        createAddCourseScreen();
                    } else if (accountType == TEACHER_OPTION) {
                        createCreateCourseScreen();
                    }
                } else {
                    client.setActiveCourse(Integer.parseInt(courseChosen));
                    createCourseMenu();
                }
            } else if (actionCommand.equals("quit")) {
                int closeConfirmation = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to quit?", "Quit?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (closeConfirmation == JOptionPane.YES_OPTION) {
                    client.close();
                    frame.dispose();
                }
            }
        }
    };

    //TODO: remove this method before submission
    /**
     * Constructor for testing without access to client
     */
    private View() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });
    }

    /**
     * Sets up View object and Event Dispatch Thread when called by Client
     * Structure borrowed from Week 11 Wednesday lecture slide 75
     *
     * @param client the Client object used for this session
     */
    View(Client client) {
        this.client = client;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });
    }

    /**
     * Sets up initial components of GUI
     */
    private void createGUI() {
        frame = new JFrame("Online Quiz Navigator v2");
        mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);

        createConnectionScreen();

        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Displays text fields for domain name and port number
     * as well as a submit button
     */
    private void createConnectionScreen() {
        JLabel domainNameLabel = new JLabel("Domain Name:");
        JTextField domainNameTxt = new JTextField(30);
        activeComponents.add(domainNameTxt);
        JLabel portNumberLabel = new JLabel("Port Number:");
        JTextField portNumberTxt = new JTextField(30);
        activeComponents.add(portNumberTxt);

        JPanel domainNamePanel = new JPanel(new FlowLayout());
        JPanel portNumberPanel = new JPanel(new FlowLayout());
        domainNamePanel.add(domainNameLabel);
        domainNamePanel.add(domainNameTxt);
        portNumberPanel.add(portNumberLabel);
        portNumberPanel.add(portNumberTxt);

        JButton submitButton = new JButton("Submit");
        submitButton.setActionCommand("send connection info");
        submitButton.addActionListener(actionListener);

        mainPanel.add(domainNamePanel, BorderLayout.NORTH);
        mainPanel.add(portNumberPanel, BorderLayout.CENTER);
        mainPanel.add(submitButton, BorderLayout.SOUTH);
    }

    /**
     * Displays text fields for username and password,
     * a set of radio buttons for selecting a student or teacher account,
     * as well as a login button and a create account button
     */
    private void createLoginScreen() {
        mainPanel.removeAll();
        activeComponents.clear();

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameTxt = new JTextField(30);
        activeComponents.add(usernameTxt);
        JLabel passwordLabel = new JLabel("Password:");
        JTextField passwordTxt = new JTextField(30);
        activeComponents.add(passwordTxt);

        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setActionCommand("send account creation info");
        createAccountButton.addActionListener(actionListener);
        JButton loginButton = new JButton("Login");
        loginButton.setActionCommand("send login info");
        loginButton.addActionListener(actionListener);

        JRadioButton studentButton = new JRadioButton("Student", true);
        studentButton.setActionCommand("student");
        JRadioButton teacherButton = new JRadioButton("Teacher");
        teacherButton.setActionCommand("teacher");
        ButtonGroup teacherOrStudentGroup = new ButtonGroup();
        teacherOrStudentGroup.add(studentButton);
        teacherOrStudentGroup.add(teacherButton);
        activeComponents.add(studentButton);
        activeComponents.add(teacherButton);

        JPanel usernamePanel = new JPanel(new FlowLayout());
        JPanel passwordPanel = new JPanel(new FlowLayout());
        JPanel teacherOrStudentPanel = new JPanel(new FlowLayout());
        JPanel loginPanel = new JPanel(new BorderLayout());
        JPanel submitPanel = new JPanel(new FlowLayout());
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameTxt);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordTxt);
        teacherOrStudentPanel.add(studentButton);
        teacherOrStudentPanel.add(teacherButton);
        loginPanel.add(usernamePanel, BorderLayout.NORTH);
        loginPanel.add(passwordPanel, BorderLayout.CENTER);
        loginPanel.add(teacherOrStudentPanel, BorderLayout.SOUTH);
        submitPanel.add(createAccountButton);
        submitPanel.add(loginButton);

        mainPanel.add(loginPanel, BorderLayout.CENTER);
        mainPanel.add(submitPanel, BorderLayout.SOUTH);

        mainPanel.validate();
        mainPanel.repaint();
    }

    /**
     * Displays a list of courses the user currently has and an "add course" option,
     * as well as a submission button
     */
    private void createMainMenu() {
        mainPanel.removeAll();
        activeComponents.clear();

        ArrayList<Course> courseList = client.getAccountCourses();
        JPanel coursePanel = new JPanel(new GridLayout(0, 1));
        ButtonGroup courseGroup = new ButtonGroup();

        for (Course currentCourse : courseList) {
            int courseNumber = currentCourse.getCourseNumber();
            String courseName = currentCourse.getCourseName();
            String displayCourse = courseNumber + ": " + courseName;

            JRadioButton courseButton = new JRadioButton(displayCourse);
            courseButton.setActionCommand(Integer.toString(courseNumber));
            courseGroup.add(courseButton);
            coursePanel.add(courseButton);
        }

        JRadioButton addCourseButton = new JRadioButton("Add course", true);
        addCourseButton.setActionCommand("add course");
        courseGroup.add(addCourseButton);
        coursePanel.add(addCourseButton);

        activeComponents.add(courseGroup);

        JButton selectButton = new JButton("Select");
        selectButton.setActionCommand("choose course");
        selectButton.addActionListener(actionListener);
        JButton quitButton = new JButton("Quit");
        quitButton.setActionCommand("quit");
        quitButton.addActionListener(actionListener);
        JPanel selectOrQuitPanel = new JPanel(new FlowLayout());
        selectOrQuitPanel.add(selectButton);
        selectOrQuitPanel.add(quitButton);

        JScrollPane scrollPane = new JScrollPane(coursePanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(selectOrQuitPanel, BorderLayout.SOUTH);

        mainPanel.validate();
        mainPanel.repaint();
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private void createAddCourseScreen() {
        ArrayList<Course> courseList = client.getAllCourses();
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private void createCreateCourseScreen() {
        String courseName = "";
        int courseNumber = 0;
        client.createCourse(courseName, courseNumber);
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private void createCourseMenu() {
        ArrayList<Quiz> quizList = client.getQuizzes();
        int quizNumber = 0;
        client.setActiveQuiz(quizNumber);
        client.clearActiveCourse();
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private void createStudentQuizOptionsMenu() {

    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private void createTeacherQuizOptionsMenu() {
        client.deleteQuiz();
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private void createCreateQuizIntroScreen() {
        //note: this should handle or call the file import option
        File file = new File();
        client.addImportedQuiz(file);
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private void createCreateQuizTitleScreen() {
        Quiz quiz = new Quiz(new Scanner(System.in), new Course("", new Teacher("", ""), 0));
        client.createQuiz(quiz);
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private void createCreateQuestionScreen() {
        Question question = new Question();
        client.addQuestionToQuiz(question);
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private void createEditQuizMenu() {
        ArrayList<Question> questions = client.getQuestions();
        int questionNumber = 0;
        client.setActiveQuestion(questionNumber);
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private void createEditQuestionScreen() {
        client.deleteQuestion();
        Question question = new Question();
        client.updateQuestion(question);
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private void createTakeQuizIntroScreen() {

    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private void createActiveQuizScreen() {
        ArrayList<Question> questions = client.getQuestions();
        Quiz quiz = new Quiz(new Scanner(System.in), new Course("", new Teacher("", ""), 0));
        Submission submission = new Submission(new Student("", ""), quiz);
        client.submitSubmission(submission);
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private JPanel assembleTrueFalseQuestion() {
        return new JPanel();
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private JPanel assembleMultipleChoiceQuestion() {
        return new JPanel();
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private JPanel assembleFillInTheBlankQuestion() {
        return new JPanel();
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private void createSubmissionMenu() {
        ArrayList<Submission> submissions = client.getSubmissions();
        client.setActiveSubmission(submissionNumber);
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    private void createSubmissionViewer() {
        Quiz quiz = client.getCurrentQuiz();
        ArrayList<Question> questions = client.getQuestions();
        client.clearActiveSubmission();
    }

    //NOTE: this method has not been implemented yet. All calls to Client methods are for reference, since I expect to
    //use that method in the actual implementation. I have described various details of the needed methods at the top of
    //the page directly under the import statements.
    /**
     * Updates view being displayed to be consistent with new server updates
     */
    public void update() {

    }

    //TODO: remove before submission
    /**
     * Main method for testing
     *
     * @param args
     */
    public static void main(String[] args) {
        View view = new View();
    }
}
