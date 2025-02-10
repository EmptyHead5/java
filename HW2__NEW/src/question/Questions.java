package question;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Questions {
    private List<Question> questionList;

    public Questions() {
        this.questionList = new ArrayList<>();
    }

    // 创建新的问题
    public void addQuestion(Question question) {
        questionList.add(question);
    }

    // 获取所有问题
    public List<Question> getAllQuestions() {
        return questionList;
    }

    // 通过ID查找问题
    public Optional<Question> getQuestionById(int id) {
        return questionList.stream().filter(q -> q.getId() == id).findFirst();
    }

    // 更新指定ID的问题内容
    public boolean updateQuestion(int id, String newContent) {
        Optional<Question> question = getQuestionById(id);
        if (question.isPresent()) {
            question.get().setContent(newContent);
            return true;
        }
        return false;
    }

    // 删除指定ID的问题
    public boolean deleteQuestion(int id) {
        return questionList.removeIf(q -> q.getId() == id);
    }
    
    public List<Question> searchQuestions(String keyword) {
        return questionList.stream()
                .filter(q -> q.getContent().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
}
