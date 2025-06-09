package dev.fredericoAkira.FtoA.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.DTO.LovDTO.LovDTO;
import dev.fredericoAkira.FtoA.DTO.MaterialDTO.MaterialDetailAdminDTO;
import dev.fredericoAkira.FtoA.DTO.MaterialDTO.MaterialListAdminDTO;
import dev.fredericoAkira.FtoA.DTO.MaterialDTO.MaterialListUserDTO;
import dev.fredericoAkira.FtoA.DTO.RequestDTO.MaterialEditReq;
import dev.fredericoAkira.FtoA.Entity.Material;
import dev.fredericoAkira.FtoA.Entity.Quiz;
import dev.fredericoAkira.FtoA.Entity.Topic;
import dev.fredericoAkira.FtoA.Entity.User;
import dev.fredericoAkira.FtoA.Repository.MaterialRepository;
import dev.fredericoAkira.FtoA.Repository.QuizRepository;
import dev.fredericoAkira.FtoA.Repository.TopicRepository;
import dev.fredericoAkira.FtoA.Repository.UserRepository;
import dev.fredericoAkira.FtoA.Util.PropertyCopyUtil;
import dev.fredericoAkira.FtoA.Util.SortingUtil;

@Service
public class MaterialService {
    @Autowired
    private MaterialRepository matRepo;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository usRepo;

    @Autowired
    private QuizRepository quizRepository;

    public ApiResponse<?> addMaterial(Material request, String userId){
        Material material = new Material();
        material.setMaterialName(request.getMaterialName());
        material.setDescription(
            (request.getDescription() == null || request.getDescription().isEmpty()) ? null : request.getDescription()
        );
        material.setBackgroundImg(request.getBackgroundImg());
        material.setQuizzes(request.getQuizzes());
        material.setTopics(request.getTopics());
        material.setGrade(request.getGrade());
        material.setDifficulty(request.getDifficulty());

        User user = usRepo.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not Found"));
        user.setLastMaterial(material.getMaterialName());
        usRepo.save(user);

        matRepo.save(material);

        return ApiResponse.success(null);
    }

    public ApiResponse<?> editMaterial(MaterialEditReq request){
        Material material = matRepo.findById(new ObjectId(request.getMaterialId())).orElseThrow(() -> new RuntimeException("Material not found"));
        PropertyCopyUtil.copyNonNullProperties(request, material);

        matRepo.save(material);

        return ApiResponse.success("Material Updated Successfully");
    }

    public ApiResponse<?> deleteMaterial(String materialId){
        Material material = matRepo.findById(new ObjectId(materialId)).orElseThrow(() -> new RuntimeException("Material not found"));
        matRepo.delete(material);

        return ApiResponse.success("Material Deleted Successfully");
    }

    public ApiResponse<Page<MaterialListUserDTO>>getMaterialList(
        String userId,
        String gradeFilter,
        String difficultyFilter,
        String searchQuery,
        Pageable pageable
    ) throws UsernameNotFoundException {
        User user = usRepo.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        List<Material> materials = matRepo.findAll();
        List<Topic> topics = topicRepository.findAll();

        List<String> grades = matRepo.findAllGrades();

        int maxGrade = grades.stream()
            .filter(grade -> grade.matches("\\d+")) // Keep only numeric grades
            .map(Integer::parseInt)
            .max(Integer::compare)
            .orElse(1); // Default to 1 if empty
        int userGrade = (user.getGrade() != null && !user.getGrade().trim().isEmpty() && user.getGrade().matches("\\d+"))
            ? Integer.parseInt(user.getGrade())
            : 1;
        List<Integer> gradeOrder = SortingUtil.getSortedGradeOrder(userGrade, maxGrade);

        final String finalGradeFilter = (gradeFilter != null && gradeFilter.trim().isEmpty()) ? null : gradeFilter;
        final String finalSearchQuery = (searchQuery != null && searchQuery.trim().isEmpty()) ? null : searchQuery;
        final String finalDifficultyFilter = (difficultyFilter != null && difficultyFilter.trim().isEmpty()) ? null : difficultyFilter;

        materials = materials.stream()
            .sorted(Comparator.comparingInt(material -> gradeOrder.indexOf(userGrade)))
            .filter(material -> (finalDifficultyFilter == null || material.getDifficulty().equalsIgnoreCase(finalDifficultyFilter)))
            .filter(material -> (finalGradeFilter == null || material.getGrade().equalsIgnoreCase(finalGradeFilter)))
            .filter(material -> (finalSearchQuery == null || finalSearchQuery.isEmpty() ||
                    material.getMaterialName().toLowerCase().contains(finalSearchQuery.toLowerCase())))
            .collect(Collectors.toList());
        
        Map<String, String> topicIdToName = topics.stream()
            .collect(Collectors.toMap(
                topic -> topic.getTopicId() == null ? "UNKNOWN_ID" : topic.getTopicId().toString(),
                topic -> topic.getTopicName() == null ? "UNKNOWN_NAME" : topic.getTopicName()
            ));
        
        List<MaterialListUserDTO> materialList = materials.stream()
            .map(material -> new MaterialListUserDTO(
                material.getMaterialName(),
                material.getDescription(),
                material.getGrade(),
                material.getTopics() != null
                    ? material.getTopics().stream()
                        .map(topicId -> topicIdToName.getOrDefault(topicId, "Unknown Topic"))
                        .collect(Collectors.toList())
                    : List.of(),
                material.getDifficulty()
            )).collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), materialList.size());
        List<MaterialListUserDTO> pagedMaterials = materialList.subList(start, end);

        return ApiResponse.success(new PageImpl<>(pagedMaterials, pageable, materialList.size()));
    }

    public ApiResponse<Page<MaterialListUserDTO>>getMaterialListQuiz(
        String userId,
        String gradeFilter,
        String difficultyFilter,
        String searchQuery,
        Pageable pageable
    ) throws UsernameNotFoundException {
        User user = usRepo.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        List<Material> materials = matRepo.findAll().stream()
            .filter(material -> material.getQuizzes() != null && !material.getQuizzes().isEmpty())
            .collect(Collectors.toList());

        List<String> grades = matRepo.findAllGrades();

        int maxGrade = grades.stream()
            .filter(grade -> grade.matches("\\d+")) // Keep only numeric grades
            .map(Integer::parseInt)
            .max(Integer::compare)
            .orElse(1); // Default to 1 if empty
        int userGrade = (user.getGrade() != null && !user.getGrade().trim().isEmpty() && user.getGrade().matches("\\d+"))
            ? Integer.parseInt(user.getGrade())
            : 1;
        List<Integer> gradeOrder = SortingUtil.getSortedGradeOrder(userGrade, maxGrade);

        final String finalGradeFilter = (gradeFilter != null && gradeFilter.trim().isEmpty()) ? null : gradeFilter;
        final String finalSearchQuery = (searchQuery != null && searchQuery.trim().isEmpty()) ? null : searchQuery;
        final String finalDifficultyFilter = (difficultyFilter != null && difficultyFilter.trim().isEmpty()) ? null : difficultyFilter;

        materials = materials.stream()
            .sorted(Comparator.comparingInt(material -> gradeOrder.indexOf(userGrade)))
            .filter(material -> (finalDifficultyFilter == null || material.getDifficulty().equalsIgnoreCase(finalDifficultyFilter)))
            .filter(material -> (finalGradeFilter == null || material.getGrade().equalsIgnoreCase(finalGradeFilter)))
            .filter(material -> (finalSearchQuery == null || finalSearchQuery.isEmpty() ||
                    material.getMaterialName().toLowerCase().contains(finalSearchQuery.toLowerCase())))
            .collect(Collectors.toList());
        
        List<MaterialListUserDTO> materialList = materials.stream()
            .map(material -> new MaterialListUserDTO(
                material.getMaterialName(),
                material.getDescription(),
                material.getGrade(),
                List.of(),
                material.getDifficulty()
            )).collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), materialList.size());
        List<MaterialListUserDTO> pagedMaterials = materialList.subList(start, end);

        return ApiResponse.success(new PageImpl<>(pagedMaterials, pageable, materialList.size()));
    }

    public ApiResponse<Page<MaterialListAdminDTO>>getMaterialListAdmin(
        String userId,
        String gradeFilter,
        String searchQuery,
        Pageable pageable
    ) throws UsernameNotFoundException {
        User user = usRepo.findById(new ObjectId(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        List<Material> materials = matRepo.findAll();
        List<Topic> topics = topicRepository.findAll();
        List<Quiz> quizzes = quizRepository.findAll();

        List<String> grades = matRepo.findAllGrades();
        int maxGrade = grades.stream()
            .filter(grade -> grade.matches("\\d+"))
            .map(Integer::parseInt)
            .max(Integer::compare)
            .orElse(1); // Default to 1 if empty

            int userGrade = (user.getGrade() != null && user.getGrade().matches("\\d+")) 
            ? Integer.parseInt(user.getGrade())
            : 1; // Default to 1 if null or invalid
        
        List<Integer> gradeOrder = SortingUtil.getSortedGradeOrder(userGrade, maxGrade);
        // List<Integer> gradeOrder = SortingUtil.getSortedGradeOrder(Integer.parseInt(user.getGrade()), maxGrade);
        
        final String finalGradeFilter = (gradeFilter != null && gradeFilter.trim().isEmpty()) ? null : gradeFilter;
        final String finalSearchQuery = (searchQuery != null && searchQuery.trim().isEmpty()) ? null : searchQuery;

        materials = materials.stream()
            .sorted(Comparator.comparingInt(material -> gradeOrder.indexOf(userGrade)))
            .filter(material -> (finalGradeFilter == null || material.getGrade().equalsIgnoreCase(finalGradeFilter)))
            .filter(material -> (finalSearchQuery == null || finalSearchQuery.isEmpty() ||
                    material.getMaterialName().toLowerCase().contains(finalSearchQuery.toLowerCase())))
            .collect(Collectors.toList());
        
        Map<String, String> topicIdToName = topics.stream()
            .collect(Collectors.toMap(
                topic -> topic.getTopicId() == null ? "UNKNOWN_ID" : topic.getTopicId().toString(),
                topic -> topic.getTopicName() == null ? "UNKNOWN_NAME" : topic.getTopicName()
            ));
        
        Map<String, String> quizIdToName = quizzes.stream()
            .collect(Collectors.toMap(
                quiz -> quiz.getQuizId() == null ? "UNKNOWN_ID" : quiz.getQuizId().toString(),
                quiz -> quiz.getQuizName() == null ? "UNKNOWN_Name" : quiz.getQuizName()
            ));
        
        List<MaterialListAdminDTO> materialList = materials.stream()
            .map(material -> new MaterialListAdminDTO(
                material.getMaterialId().toString(),
                material.getMaterialName(),
                material.getTopics() != null
                    ? material.getTopics().stream()
                        .map(topicId -> topicIdToName.getOrDefault(topicId, "Unknown Topic"))
                        .collect(Collectors.toList())
                    : List.of(),
                material.getQuizzes() != null
                    ? material.getQuizzes().stream()
                        .map(quizId -> quizIdToName.getOrDefault(quizId, "Unknown Quiz"))
                        .collect(Collectors.toList())
                    : List.of(),
                material.getGrade(),
                material.getDifficulty()
            )).collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), materialList.size());
        List<MaterialListAdminDTO> pagedMaterials = materialList.subList(start, end);

        return ApiResponse.success(new PageImpl<>(pagedMaterials, pageable, materialList.size()));
    }

    public ApiResponse<MaterialDetailAdminDTO>getMaterialDetailAdmin(String materialName) throws UsernameNotFoundException {
        Material materials = matRepo.findByMaterialName(materialName).orElseThrow(() -> new RuntimeException("Material not Found"));
        List<ObjectId> topicObjectIds = materials.getTopics().stream()
            .map(ObjectId::new)
            .collect(Collectors.toList());
        List<ObjectId> quizObjectIds = materials.getQuizzes().stream()
            .map(ObjectId::new)
            .collect(Collectors.toList());
        MaterialDetailAdminDTO body = new MaterialDetailAdminDTO(
            materials.getMaterialId().toString(),
            materials.getMaterialName(),
            materials.getDescription(),
            materials.getBackgroundImg(),
            materials.getGrade(),
            materials.getDifficulty(),
            topicRepository.findAllById(topicObjectIds).stream()
                .map(topic -> new LovDTO(
                    topic.getTopicName(),
                    topic.getTopicId().toString()
                ))
                .collect(Collectors.toList()),
            quizRepository.findAllById(quizObjectIds).stream()
                .map(quiz -> new LovDTO(
                    quiz.getQuizName(),
                    quiz.getQuizId().toString()
                ))
                .collect(Collectors.toList())
        );

        return ApiResponse.success(body);
    }

    public ApiResponse<MaterialListUserDTO>getMaterialDetail(
        String materialName
    ) throws UsernameNotFoundException {
        Material material = matRepo.findByMaterialName(materialName).orElseThrow(() -> new RuntimeException("User not found"));
        List<Topic> topics = topicRepository.findAll();

        Map<String, String> topicIdToName = topics.stream()
            .collect(Collectors.toMap(
                topic -> topic.getTopicId() == null ? "UNKNOWN_ID" : topic.getTopicId().toString(),
                topic -> topic.getTopicName() == null ? "UNKNOWN_NAME" : topic.getTopicName()
            ));
        
        MaterialListUserDTO materialDetail =  new MaterialListUserDTO(
                material.getMaterialName(),
                material.getDescription(),
                material.getGrade(),
                material.getTopics() != null
                    ? material.getTopics().stream()
                        .map(topicId -> topicIdToName.getOrDefault(topicId, "Unknown Topic"))
                        .collect(Collectors.toList())
                    : List.of(),
                material.getDifficulty()
        );

        return ApiResponse.success(materialDetail);
    }
}
