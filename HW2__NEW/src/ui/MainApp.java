package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import question.Question;
import question.Questions;
import answers.Answer;
import answers.Answers;

import java.util.List;
import java.util.Optional;

public class MainApp extends Application {

    private Questions questions;
    private Answers answers;
    private ListView<String> navigationList;
    private TextArea answerArea;

    @Override
    public void start(Stage primaryStage) {
   
        initData();

   
        BorderPane root = new BorderPane();


        navigationList = new ListView<>();
        refreshQuestionList();  

   
        Label questionTitle = new Label("选择一个问题查看详情");
        questionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10;");

        TextArea questionContent = new TextArea();
        questionContent.setWrapText(true);
        questionContent.setEditable(false);

        // 使用类级别的 answerArea
        answerArea = new TextArea("暂无回答");
        answerArea.setWrapText(true);
        answerArea.setEditable(false);

        TextArea userAnswerInput = new TextArea();
        userAnswerInput.setPromptText("输入新的答案...");
        userAnswerInput.setPrefHeight(80);

        Button updateAnswerButton = new Button("提交新答案");
        updateAnswerButton.setOnAction(e -> {
            String selectedQuestion = navigationList.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null && !userAnswerInput.getText().isEmpty()) {
                int questionId = getQuestionIdByTitle(selectedQuestion);
                answers.addAnswer(new Answer(answers.getAllAnswers().size() + 1, userAnswerInput.getText(), questionId));
                updateAnswerArea(questionId); 
                userAnswerInput.clear();
            }
        });

        // 新问题提交部分
        TextField newQuestionTitleField = new TextField();
        newQuestionTitleField.setPromptText("输入新问题标题...");

        TextArea newQuestionContentArea = new TextArea();
        newQuestionContentArea.setPromptText("输入新问题内容...");
        newQuestionContentArea.setPrefHeight(80);

        Button submitQuestionButton = new Button("提交新问题");
        submitQuestionButton.setOnAction(e -> {
            String title = newQuestionTitleField.getText().trim();
            String content = newQuestionContentArea.getText().trim();

            // 检查输入是否为空
            if (title.isEmpty() || content.isEmpty()) {
                showAlert("输入错误", "问题标题和内容不能为空！");
                return;
            }

            // 生成新问题 ID
            int newId = questions.getAllQuestions().size() + 1;

            // 创建新问题并添加到数据结构中
            Question newQuestion = new Question(newId,title,content);
            questions.addQuestion(newQuestion);

            // 刷新导航列表并清空输入框
            refreshQuestionList();
            newQuestionTitleField.clear();
            newQuestionContentArea.clear();

            // 提示用户成功添加问题
            showAlert("添加成功", "新问题已成功添加！");
        });

        
        Button editQuestionButton = new Button("edit");
        editQuestionButton.setOnAction(e -> {
            String selectedQuestion = navigationList.getSelectionModel().getSelectedItem();
            if (selectedQuestion == null) {
                showAlert("未选中问题", "请选择你要修改的问题！");
                return;
            }

            int questionID = getQuestionIdByTitle(selectedQuestion);
            Optional<Question> question = questions.getQuestionById(questionID);

            if (question.isPresent()) {
                Question currentQuestion = question.get();

                // 弹出对话框让用户输入新的问题内容
                TextInputDialog dialog = new TextInputDialog(currentQuestion.getContent());
                dialog.setTitle("编辑问题");
                dialog.setHeaderText("编辑选中的问题");
                dialog.setContentText("新问题内容：");

                Optional<String> newContent = dialog.showAndWait();
                newContent.ifPresent(content -> {
                    if (!content.trim().isEmpty()) {
                        currentQuestion.setContent(content);  // 更新问题内容
                        refreshQuestionList();  // 刷新问题列表
                        navigationList.getSelectionModel().select(content);  // 自动选中新问题
                        showAlert("修改成功", "问题内容已更新！");
                    } else {
                        showAlert("输入错误", "问题内容不能为空！");
                    }
                });
            } else {
                showAlert("错误", "无法找到选中的问题！");
            }
        });


        Button deleteQuestionButton = new Button("删除问题");
        deleteQuestionButton.setOnAction(e -> {
            String selectedQuestion = navigationList.getSelectionModel().getSelectedItem();
            if (selectedQuestion == null) {
                showAlert("未选中问题", "请选择要删除的问题！");
                return;
            }

            // 获取选中问题的 ID
            int questionId = getQuestionIdByTitle(selectedQuestion);
            Optional<Question> question = questions.getQuestionById(questionId);
            
            if (question.isPresent()) {
                // 确认删除
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("确认删除");
                confirmationAlert.setHeaderText(null);
                confirmationAlert.setContentText("确定要删除该问题及其所有答案吗？");

                Optional<ButtonType> result = confirmationAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // 删除问题和对应的答案
                    questions.deleteQuestion(questionId);
                    answers.deleteAnswersByQuestionId(questionId);
                    
                    // 刷新问题列表和回答区域
                    refreshQuestionList();
                    answerArea.setText("暂无回答");
                }
            } else {
                showAlert("问题不存在", "未找到要删除的问题！");
            }
        });
        Button refreshQuestionsButton = new Button("刷新问题列表");
        refreshQuestionsButton.setOnAction(e -> refreshQuestionList());

        HBox controlPanel = new HBox(10, submitQuestionButton, deleteQuestionButton, refreshQuestionsButton,editQuestionButton);
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setPadding(new Insets(10));

        VBox newQuestionArea = new VBox(10, new Label("添加新问题"), newQuestionTitleField, newQuestionContentArea, controlPanel);
        newQuestionArea.setPadding(new Insets(10));
        newQuestionArea.setAlignment(Pos.TOP_LEFT);

        VBox contentArea = new VBox(10, questionTitle, questionContent, new Label("现有回答："), answerArea, userAnswerInput, updateAnswerButton, newQuestionArea);
        contentArea.setPadding(new Insets(15));
        contentArea.setAlignment(Pos.TOP_LEFT);

        // 监听导航选择并更新右侧内容
        navigationList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                questionTitle.setText(newValue);
                int questionId = getQuestionIdByTitle(newValue);
                questionContent.setText(questions.getQuestionById(questionId).get().getContent());
                updateAnswerArea(questionId);
            }
        });

        
        

        TextField searchField = new TextField();
        searchField.setPromptText("输入要搜索的问题关键词");


        Button searchButton = new Button("搜索");
        searchButton.setOnAction(e -> {
            String keyword = searchField.getText();
            if (!keyword.isEmpty()) {
                List<Question> searchResults = questions.searchQuestions(keyword);
                navigationList.getItems().clear();
                searchResults.forEach(q -> navigationList.getItems().add(q.getContent()));
            } else {
                refreshQuestionList();  
            }
        });
        
        VBox searchArea = new VBox(10, new Label("搜索问题"), searchField, searchButton);
        searchArea.setPadding(new Insets(10));
        searchArea.setAlignment(Pos.CENTER_LEFT);
        root.setTop(searchArea);

        
        root.setLeft(navigationList);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 950, 650);
        primaryStage.setTitle("My App");
        primaryStage.setScene(scene);
        primaryStage.show();

    }


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
                .filter(q -> q.getTitle().equals(title))  
                .findFirst()
                .map(Question::getId)
                .orElse(-1);
    }

    // 刷新导航列表
    private void refreshQuestionList() {
        navigationList.getItems().clear();
        questions.getAllQuestions().forEach(q -> navigationList.getItems().add(q.getTitle())); //将加入的问题标题更新在导航页面内
    }

    // 更新右侧回答区域
    private void updateAnswerArea(int questionId) {
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

    // 显示警告
    private void showAlert(String title,String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
