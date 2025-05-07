package tn.esprit.pidev.gestion_rdv.enteties;

public class TodoTask {
    private String id;
    private String content;
    private String description;
    private String due;
    private boolean completed;
    private String project_id;
    private String[] labels;

    // Constructor
    public TodoTask() {
    }

    // Full constructor
    public TodoTask(String id, String content, String description, String due, boolean completed, String project_id, String[] labels) {
        this.id = id;
        this.content = content;
        this.description = description;
        this.due = due;
        this.completed = completed;
        this.project_id = project_id;
        this.labels = labels;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    @Override
    public String toString() {
        return "TodoTask{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", description='" + description + '\'' +
                ", due='" + due + '\'' +
                ", completed=" + completed +
                '}';
    }
}
