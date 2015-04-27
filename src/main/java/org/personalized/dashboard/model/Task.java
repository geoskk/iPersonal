package org.personalized.dashboard.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by sudan on 9/4/15.
 */
public class Task {

    private String taskId;
    private Priority priority;
    private String name;
    private String task;
    private int percentCompletion = 0;

    public Task(){

    }

    public Task(String taskId, Priority priority, String name, String task){
        this.taskId = taskId;
        this.priority = priority;
        this.name = name;
        this.task = task;

    }

    public Task(String taskId, Priority priority, String name, String task, int percentCompletion){
        this.taskId = taskId;
        this.priority = priority;
        this.name = name;
        this.task = task;
        this.percentCompletion = percentCompletion;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getPercentCompletion(){
        return percentCompletion;
    }

    public void setPercentCompletion(int percentCompletion){
        this.percentCompletion = percentCompletion;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(this)
                .append("taskId", taskId)
                .append("priority", priority)
                .append("name", name)
                .append("task", task)
                .append("percentCompletion", percentCompletion)
                .toString();
    }

}