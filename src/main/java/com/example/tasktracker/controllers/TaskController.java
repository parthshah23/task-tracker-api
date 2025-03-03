package com.example.tasktracker.controllers;

import com.example.tasktracker.models.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final Set<Task> tasks = ConcurrentHashMap.newKeySet();
    private final AtomicInteger taskCounter = new AtomicInteger(1);
    private final Queue<Integer> availableTaskNumbers = new LinkedList<>();

    @GetMapping
    public Set<Task> getAllTasks() {
        return tasks;
    }

    @PostMapping
    public ResponseEntity<Object> createOrUpdateTasks(@RequestBody Set<Task> newTasks) {
        var updatedTasks = new HashSet<Task>();
        var unchangedTasks = new HashSet<String>();

        // ✅ Validate all fields before processing
        for (Task task : newTasks) {
            if (task.taskNumber() == null || task.title() == null || task.description() == null || task.status() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "All fields (taskNumber, title, description, status) are required."));
            }
        }

        newTasks.forEach(task -> tasks.stream()
            .filter(t -> t.equals(task))
            .findFirst()
            .ifPresentOrElse(existingTask -> {
                if (!existingTask.status().equals(task.status())) {
                    tasks.remove(existingTask);
                    var updatedTask = new Task(existingTask.id(), existingTask.taskNumber(), task.title(), task.description(), task.status());
                    tasks.add(updatedTask);
                    updatedTasks.add(updatedTask);
                } else {
                    unchangedTasks.add("Task '" + task.title() + "' is unchanged.");
                }
            }, () -> {
                int taskNumber = (task.taskNumber() > 0) 
                                 ? task.taskNumber() 
                                 : (availableTaskNumbers.isEmpty() ? taskCounter.getAndIncrement() : availableTaskNumbers.poll());

                var newTask = new Task((long) taskNumber, taskNumber, task.title(), task.description(), task.status());
                tasks.add(newTask);
                updatedTasks.add(newTask);
            }));

        return ResponseEntity.ok(Map.of("updated_tasks", updatedTasks, "unchanged_tasks", unchangedTasks));
    }

    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable Long id) {
        Optional<Task> taskToDelete = tasks.stream().filter(task -> task.id().equals(id)).findFirst();

        if (taskToDelete.isPresent()) {
            tasks.remove(taskToDelete.get());
            availableTaskNumbers.add(taskToDelete.get().taskNumber());
            return "Task deleted successfully!";
        } else {
            return "Task not found!";
        }
    }
}
