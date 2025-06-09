package dev.fredericoAkira.FtoA.Service.Student;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.LovDTO.DataDTO;
import dev.fredericoAkira.FtoA.DTO.StudentDTO.GroupDetailDTO;
import dev.fredericoAkira.FtoA.DTO.StudentDTO.GroupListDTO;
import dev.fredericoAkira.FtoA.DTO.StudentDTO.GroupSTD;
import dev.fredericoAkira.FtoA.Entity.Group;
import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Repository.GroupRepository;
import dev.fredericoAkira.FtoA.Repository.UserRepository;
import dev.fredericoAkira.FtoA.Util.PropertyCopyUtil;

@Service
public class GroupService {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    public ApiResponse<?> checkGroup(Group request){
        List<Group> existingGroups = groupRepository.findByTeacherId(request.getTeacherId());

        for (Group group : existingGroups) {
            List<String> existingStudentIds = group.getStudentIds();
            List<String> requestedStudentIds = request.getStudentIds();

            if (existingStudentIds != null && requestedStudentIds != null &&
                existingStudentIds.size() == requestedStudentIds.size() &&
                new HashSet<>(existingStudentIds).equals(new HashSet<>(requestedStudentIds))) {
                // Same teacherId and same studentIds (ignoring order)
                return ApiResponse.success("exist");
            }
        }
        return ApiResponse.success("not exist");
    }

    public ApiResponse<?> addGroup(Group request) {
        Group newGroup = new Group();
        newGroup.setGroupName(request.getGroupName());
        newGroup.setGroupName(request.getGroupName());
        newGroup.setTeacherId(request.getTeacherId());
        newGroup.setStudentIds(request.getStudentIds());
        
        groupRepository.save(newGroup);
        return ApiResponse.success("Group created");
    }

    public ApiResponse<?> editGroup(Group request) {
        Group group = groupRepository.findById(request.getGroupId()).orElseThrow(() -> new RuntimeException("Group not found"));
        PropertyCopyUtil.copyNonNullProperties(request, group);

        
        groupRepository.save(group);
        return ApiResponse.success("Group created");
    }

    public ApiResponse<?> deleteGroup(String groupId) {
        groupRepository.deleteById(new ObjectId(groupId));
        return ApiResponse.success("Group deleted");
    }

    public ApiResponse<List<GroupListDTO>> getGroupsByTeacher(String teacherId) {
        List<Group> groups = groupRepository.findByTeacherId(teacherId);

        List<GroupListDTO> groupDTOs = groups.stream().map(group -> {
            List<ObjectId> studentObjectIds = group.getStudentIds().stream()
                .map(ObjectId::new)
                .collect(Collectors.toList());

            List<User> students = userRepository.findAllById(studentObjectIds);
            List<String> studentNames = students.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());

            List<DataDTO> dataList = IntStream.range(0, studentNames.size())
                .mapToObj(i -> new DataDTO(group.getStudentIds().get(i), studentNames.get(i)))
                .collect(Collectors.toList());

            return new GroupListDTO(
                group.getGroupId().toString(),
                group.getGroupName(),
                dataList
            );
        }).collect(Collectors.toList());
        return ApiResponse.success(groupDTOs);
    }

    public ApiResponse<GroupDetailDTO> getGroupDetails(String groupId) {
        Group group = groupRepository.findById(new ObjectId(groupId)).orElseThrow(() -> new RuntimeException("Group not found"));

        List<ObjectId> studentObjectIds = group.getStudentIds().stream()
            .map(ObjectId::new)
            .collect(Collectors.toList());
        List<User> students = userRepository.findAllById(studentObjectIds);

        List<GroupSTD> studentGroup = students.stream().map(
            student -> new GroupSTD(
                student.getUserId().toString(),
                student.getUsername(),
                student.getLevel(),
                student.getGrade()
            )).collect(Collectors.toList());
        

        GroupDetailDTO groupDetail = new GroupDetailDTO(
            group.getGroupName(),
            studentGroup
        );

        return ApiResponse.success(groupDetail);
    }

    public ApiResponse<List<GroupSTD>> getAllStudentData(String teacherId, String searchValue){
        User user = userRepository.findById(new ObjectId(teacherId)).orElseThrow(() -> new RuntimeException("teacher not found"));
        List<ObjectId> studentObjectIds = user.getStudents().stream()
            .map(ObjectId::new)
            .collect(Collectors.toList());

        List<User> students = userRepository.findAllById(studentObjectIds);
        final String finalSearchValue = (searchValue != null && searchValue.trim().isEmpty()) ? null : searchValue;

        List<GroupSTD> studentGroup = students.stream()
        .filter(student -> (finalSearchValue == null || finalSearchValue.isEmpty() ||
                    student.getUsername().toLowerCase().contains(finalSearchValue.toLowerCase())))
        .map(
            student -> new GroupSTD(
                student.getUserId().toString(),
                student.getUsername(),
                student.getLevel(),
                student.getGrade()
            )).collect(Collectors.toList());

        return ApiResponse.success(studentGroup);
    }
}

