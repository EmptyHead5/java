package answers;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



public class Answers {
    private List<Answer> answerList;

    public Answers() {
        this.answerList = new ArrayList<>();
    }

    // 添加新的答案
    public void addAnswer(Answer answer) {
        answerList.add(answer);
    }

    // 获取所有答案
    public List<Answer> getAllAnswers() {
        return answerList;
    }

    // 根据问题ID获取答案子集
    public List<Answer> getAnswersByQuestionId(int questionId) {
        return answerList.stream()
                         .filter(a -> a.getQuestionId() == questionId)
                         .collect(Collectors.toList());
    }

    // 更新答案内容
    public boolean updateAnswer(int id, String newContent) {
        for (Answer answer : answerList) {
            if (answer.getId() == id) {
                answer.setContent(newContent);
                return true;
            }
        }
        return false;
    }

    // 删除指定ID的答案
    public boolean deleteAnswer(int id) {
        return answerList.removeIf(a -> a.getId() == id);
    }
    
    public void deleteAnswersByQuestionId(int questionId) {
        answerList.removeIf(answer -> answer.getQuestionId() == questionId);
    }
    
    public List<Answer> searchAnswers(String keyword) {
        return answerList.stream()
                .filter(a -> a.getContent().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

}
