package answers;

public class Answer {
    private int id;
    private String content;
    private int questionId;  // 关联的 Question ID

    public Answer(int id, String content, int questionId) {
        this.id = id;
        this.content = content;
        this.questionId = questionId;
    }

    // 获取答案ID
    public int getId() {
        return id;
    }

    // 获取答案内容
    public String getContent() {
        return content;
    }

    // 获取关联的问题ID
    public int getQuestionId() {
        return questionId;
    }

    // 更新答案内容
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Answer ID: " + id + ", Content: " + content + ", Question ID: " + questionId;
    }
}
