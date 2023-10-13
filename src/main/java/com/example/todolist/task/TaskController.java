package com.example.todolist.task;


import com.example.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private  ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel tarefaModel, HttpServletRequest request){
        UUID idUser = (UUID) request.getAttribute("idUser");
        tarefaModel.setIdUser(idUser);
        LocalDateTime currentDate = LocalDateTime.now();

        if(currentDate.isAfter((tarefaModel.getStartAt())) ||currentDate.isAfter((tarefaModel.getEndAt()))){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de início deve ser maior do que a data atual");
        }
        if(tarefaModel.getStartAt().isAfter((tarefaModel.getEndAt()))){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de início deve ser menor do que a data de términp");
        }
        TaskModel tarefa = this.taskRepository.save(tarefaModel);
        return ResponseEntity.status(HttpStatus.OK).body(tarefa);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        UUID idUser = (UUID) request.getAttribute("idUser");
        List<TaskModel> tarefas = this.taskRepository.findByIdUser(idUser);
        return tarefas;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request){
        TaskModel task = this.taskRepository.findById(id).orElse(null);

        if(task == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tarefa não encontrada");
        }

        UUID idUser = (UUID) request.getAttribute("idUser");

        if(!task.getIdUser().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Usuário não tem permissão para alterar essa tarefa");
        }

        Utils.copyNonNullProperties(taskModel, task);

        TaskModel taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(this.taskRepository.save(taskUpdated));
    }
}
