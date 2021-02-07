package com.codesoom.assignment.controllers;

import com.codesoom.assignment.TaskNotFoundException;
import com.codesoom.assignment.application.TaskService;
import com.codesoom.assignment.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(TaskController.class)
@DisplayName("TaskController 클래스")
public class TaskControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        List<Task> tasks = new ArrayList<>();

        Task beforeTask = new Task();
        beforeTask.setTitle("Test task");
        tasks.add(beforeTask);

        given(taskService.getTasks()).willReturn(tasks);
        given(taskService.getTask(1L)).willReturn(beforeTask);
    }

    @Nested
    @DisplayName("list 메서드는")
    class Describe_list {
        @Test
        @DisplayName("OK를 리턴한다")
        void itReturnsOKHttpStatus() throws Exception {
            mockMvc.perform(get("/tasks"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Test task")));
        }
    }

    @Nested
    @DisplayName("detail 메서드는")
    class Describe_detail {
        @Nested
        @DisplayName("만약 저장되어 있는 할 일의 id가 주어진다면")
        class ContextWithValidId {
            private Long givenValidId = 1L;

            @Test
            @DisplayName("OK를 리턴한다")
            void itReturnsOKHttpStatus() throws Exception {
                mockMvc.perform(get("/tasks/" + givenValidId))
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("Test task")));
            }
        }

        @Nested
        @DisplayName("만약 저장되어 있지 않은 할 일의 id가 주어진다면")
        class ContextWithInvalidId {
            private Long givenInvalidId = 100L;

            @Test
            @DisplayName("NOT_FOUND를 리턴한다")
            void itReturnsNOT_FOUNDHttpStatus() throws Exception {
                mockMvc.perform(get("/tasks/" + givenInvalidId))
                        .andExpect(status().isNotFound());
            }
        }
    }

    @Nested
    @DisplayName("create 메서드는")
    class Describe_create {
        @Test
        @DisplayName("CREATED를 리턴한다")
        void itReturnsCREATEDHttpStatus() throws Exception {
            Task createTask = new Task();
            createTask.setTitle("Second");
            given(taskService.createTask(any())).willReturn(createTask);

            mockMvc.perform(post("/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"title\":\"Second\"}"))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("update 메서드는")
    class Describe_update {
        @Nested
        @DisplayName("만약 저장되어 있는 할 일의 id가 주어진다면")
        class ContextWithValidId {
            private Long givenValidId = 1L;

            @Test
            @DisplayName("OK를 리턴한다")
            void itReturnsOKHttpStatus() throws Exception {
                Task updateSource = new Task();
                updateSource.setTitle("new");
                given(taskService.updateTask(any(), any())).willReturn(updateSource);

                mockMvc.perform(patch("/tasks/"+ givenValidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"new\"}"))
                        .andExpect(status().isOk());
            }
        }

        @Nested
        @DisplayName("만약 저장되어 있지 않은 할 일의 id가 주어진다면")
        class ContextWithInvalidId {
            private Long givenInvalidId = 100L;

            @Test
            @DisplayName("NOT_FOUND를 리턴한다")
            void itReturnsNOT_FOUNDHttpStatus() throws Exception {
                Task updateSource = new Task();
                updateSource.setTitle("new");
                given(taskService.updateTask(any(), any())).willThrow(new TaskNotFoundException(givenInvalidId));

                mockMvc.perform(patch("/tasks/" + givenInvalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\" : null , \"title\" : \"new\"}"))
                        .andExpect(status().isNotFound());
            }
        }
    }

    @Nested
    @DisplayName("delete 메서드는")
    class Describe_delete {
        @Nested
        @DisplayName("만약 저장되어 있는 할 일의 id가 주어진다면")
        class ContextWithValidId {
            private Long givenValidId = 100L;

            @Test
            @DisplayName("NO_CONTENT를 리턴한다")
            void itReturnsNO_CONTENTHttpStatus() throws Exception {
                mockMvc.perform(delete("/tasks/" + givenValidId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNoContent());
            }
        }

        @Nested
        @DisplayName("만약 저장되어 있지 않은 항 일의 id가 주어진다면")
        class ContextWithInvalidId {
            private Long givenInvalidId = 100L;

            @Test
            @DisplayName("NOT_FOUND를 리턴한다")
            void itReturnsNOT_FOUNDHttpStatus() throws Exception {
                given(taskService.deleteTask(givenInvalidId)).willThrow(new TaskNotFoundException(100L));

                mockMvc.perform(delete("/tasks/" + givenInvalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());
            }
        }
    }
}
