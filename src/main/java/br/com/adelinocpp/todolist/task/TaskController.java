package br.com.adelinocpp.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.adelinocpp.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController 
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        var idUser = (UUID)request.getAttribute("idUser");
        taskModel.setIdUser(idUser);
        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início/término deve ser maior que a data atual.");
        }
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início precisa ser anterior a data de término.");
        }
        var task = this.taskRepository.save(taskModel);
        //System.out.println("Chegou no controller " + request.getAttribute("idUser"));
        return ResponseEntity.status(HttpStatus.OK).body(task);
    };

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        var idUser = (UUID)request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser(idUser);
        return tasks;

    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){
        //var idUser = (UUID)request.getAttribute("idUser");
        var task = this.taskRepository.findById(id).orElse(null);
        if (task == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("A tarefa que deseja atualizar não foi encontrada.");
        else{
            Utils.copyNonNullProprieties(taskModel,task);
            //taskModel.setIdUser(idUser);
            //taskModel.setId(id);
            var taskReturn = this.taskRepository.save(task);
            return ResponseEntity.status(HttpStatus.OK).body(taskReturn);
        }
    }
}
