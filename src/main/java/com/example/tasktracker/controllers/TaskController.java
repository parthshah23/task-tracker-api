package com.example.tasktracker.controllers;

import com.example.tasktracker.models.Task;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final List<Task> tasks = new ArrayList<>();
    private final AtomicLong counter = new AtomicLong(1);

    @GetMapping
    public List<Task> getAllTasks() {
        return tasks;
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        task.setId(counter.getAndIncrement());
        tasks.add(task);
        return task;
    }

    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable Long id) {
        boolean removed = tasks.removeIf(task -> task.getId().equals(id));
        return removed ? "Task deleted successfully!" : "Task not found!";
    }
}