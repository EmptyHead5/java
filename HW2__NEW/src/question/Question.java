package question;

public class Question {
    private int id;
    private String content;
    private String title;


    public Question(int id,String title,String content) {
        this.id = id;
        this.title=title;
        this.content=content;
    }



	// 获取问题ID
    public int getId() {
        return id;
    }
    
    public String getTitle()
    {
    	return title;
    }
    // 获取问题内容
    public String getContent() {
        return content;
    }

    // 更新问题内容
    public void setContent(String content) {
        this.content = content;
    }
    

    @Override
    public String toString() {
        return "Question ID: " + id + ", Content: " + content;
    }
}
