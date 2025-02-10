package mainApp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import question.Question;
import question.Questions;
import answers.Answer;
import answers.Answers;

import java.util.List;

public class EDdussion extends Application {

    private Questions questions;
    private Answers answers;

    @Override
    public void start(Stage primaryStage) {
        // 初始化问题和答案
        initData();

        // 主界面布局
        BorderPane root = new BorderPane();

        // 左侧 ListView 显示问题
        ListView<String> navigationList = new ListView<>();
        questions.getAllQuestions().forEach(q -> navigationList.getItems().add(q.getContent()));

        // 右侧内容区域
        Label questionTitle = new Label("选择一个问题查看详情");
        questionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10;");

        TextArea questionContent = new TextArea();
        questionContent.setWrapText(true);
        questionContent.setEditable(false);

        TextArea answerArea = new TextArea("暂无回答");
        answerArea.setWrapText(true);
        answerArea.setEditable(false);

        // 用户更新新答案输入框
        TextArea userAnswerInput = new TextArea();
        userAnswerInput.setPromptText("输入新的答案...");
        userAnswerInput.setPrefHeight(100);

        Button updateAnswerButton = new Button("提交新答案");
        updateAnswerButton.setOnAction(e -> {
            String selectedQuestion = navigationList.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null && !userAnswerInput.getText().isEmpty()) {
                int questionId = getQuestionIdByTitle(selectedQuestion);
                answers.addAnswer(new Answer(answers.getAllAnswers().size() + 1, userAnswerInput.getText(), questionId));
                answerArea.setText("用户提交的新答案: " + userAnswerInput.getText());
                userAnswerInput.clear();
            }
        });

        VBox contentArea = new VBox(10, questionTitle, questionContent, new Label("现有回答："), answerArea, userAnswerInput, updateAnswerButton);
        contentArea.setPadding(new Insets(15));
        contentArea.setAlignment(Pos.TOP_LEFT);

        // 监听导航选择并更新右侧内容
        navigationList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                questionTitle.setText(newValue);
                int questionId = getQuestionIdByTitle(newValue);
                questionContent.setText(questions.getQuestionById(questionId).get().getContent());
                List<Answer> answerList = answers.getAnswersByQuestionId(questionId);
                if (answerList.isEmpty()) {
                    answerArea.setText("暂无回答");
                } else {
                    StringBuilder answerText = new StringBuilder();
                    for (Answer answer : answerList) {
                        answerText.append(answer.getContent()).append("\n");
                    }
                    answerArea.setText(answerText.toString());
                }
            }
        });

        root.setLeft(navigationList);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("JavaFX - 问题和答案展示");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // 初始化数据
    private void initData() {
        questions = new Questions();
        answers = new Answers();


        questions.addQuestion(new Question(1, "What is Java?", "What is Java used for in software development?"));
        questions.addQuestion(new Question(2, "What is Polymorphism?", "How does polymorphism improve code flexibility?"));
        questions.addQuestion(new Question(3, "What is Inheritance in OOP?", "How does inheritance promote code reuse in object-oriented programming?"));
        questions.addQuestion(new Question(4, "What is an Interface?", "Explain the role of interfaces in Java and how they differ from abstract classes."));
        questions.addQuestion(new Question(5, "What is Encapsulation?", "How does encapsulation help to protect data in Java objects?"));



        answers.addAnswer(new Answer(1, "Java is a programming language.", 1));
        answers.addAnswer(new Answer(2, "Polymorphism allows objects to take on multiple forms.", 2));
        answers.addAnswer(new Answer(3, "Inheritance enables a class to acquire properties of another class.", 3));
    }

    // 根据问题标题获取问题 ID
    private int getQuestionIdByTitle(String title) {
        return questions.getAllQuestions().stream()
                .filter(q -> q.getContent().equals(title))
                .findFirst()
                .map(Question::getId)
                .orElse(-1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
