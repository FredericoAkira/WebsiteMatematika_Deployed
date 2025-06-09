package dev.fredericoAkira.FtoA.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.RequestDTO.AddStudentReq;
import dev.fredericoAkira.FtoA.Entity.Group;
import dev.fredericoAkira.FtoA.Service.Student.GroupService;
import dev.fredericoAkira.FtoA.Service.Student.StudentService;


@RestController
@RequestMapping("/api")
public class StudentController {

    @Autowired
    StudentService studentService;

    @Autowired
    GroupService groupService;

    @GetMapping("/getStudentList")
    public ResponseEntity<?> getAllNames(
        Pageable pageable,
        @RequestParam(required = false) String teacherId,
        @RequestParam(required = false) String filterLevel,
        @RequestParam(required = false) String filterGrade,
        @RequestParam(required = false) String searchQuery
    ) {
        return ResponseEntity.ok(studentService.getTableData(pageable, teacherId, filterLevel, filterGrade, searchQuery));
    }

    @PostMapping("/addStudent")
    public ResponseEntity<?> addStudent(@RequestBody AddStudentReq entity) {
        return ResponseEntity.ok(studentService.addStudent(entity.getTeacherId(), entity.getStudentIds()));
    }
    
    @GetMapping("/getStudentDetail")
    public ResponseEntity<?> getDetail (@RequestParam String studentId) {
        return ResponseEntity.ok(studentService.getStudentDetail(studentId));
    }
    
    @DeleteMapping("/deleteStudent/{teacherId}/{studentId}")
    public ResponseEntity<ApiResponse<?>> deleteTodo(@PathVariable String teacherId, @PathVariable String studentId) {
        studentService.deleteStudent(teacherId, studentId);
        return ResponseEntity.ok(ApiResponse.success("Student successfully deleted"));
    }

    @PostMapping("/addGroup")
    public ResponseEntity<?> addGroup(@RequestBody Group request) {
        groupService.addGroup(request);
        return ResponseEntity.ok(ApiResponse.success("Group successfully added"));
    }

    @PostMapping("/checkGroup")
    public ResponseEntity<?> checkGroup(@RequestBody Group request) {
        return ResponseEntity.ok(groupService.checkGroup(request));
    }

    @GetMapping("/getGroup")
    public ResponseEntity<?> getGroupTeacher(@RequestParam String teacherId) {
        return ResponseEntity.ok(groupService.getGroupsByTeacher(teacherId));
    }

    @GetMapping("/groupDetail")
    public ResponseEntity<?> getGroupDetail(@RequestParam String groupId) {
        return ResponseEntity.ok(groupService.getGroupDetails(groupId));
    }

    @GetMapping("/studentOption")
    public ResponseEntity<?> getAllStudent(
        @RequestParam String teacherId,
        @RequestParam(required = false) String searchValue
    ) {
        return ResponseEntity.ok(groupService.getAllStudentData(teacherId, searchValue));
    }

    @PostMapping("/editGroup")
    public ResponseEntity<?> editGroup(@RequestBody Group request) {
        groupService.editGroup(request);
        return ResponseEntity.ok(ApiResponse.success("Group successfully edited"));
    }

    @DeleteMapping("/deleteGroup/{groupId}")
    public ResponseEntity<ApiResponse<?>> deleteGroup(@PathVariable String groupId) {
        return ResponseEntity.ok(groupService.deleteGroup(groupId));
    }

}
