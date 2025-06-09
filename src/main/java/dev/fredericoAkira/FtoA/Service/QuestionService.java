package dev.fredericoAkira.FtoA.Service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Configuration.ApiResponse;
import dev.fredericoAkira.FtoA.Entity.Question;
import dev.fredericoAkira.FtoA.Entity.Quiz;
import dev.fredericoAkira.FtoA.Repository.QuestionRepository;
import dev.fredericoAkira.FtoA.Repository.QuizRepository;
import dev.fredericoAkira.FtoA.Util.PropertyCopyUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public String upsertQuestion(Question question) {
        if (question.getQuestionId() != null && questionRepository.existsById(question.getQuestionId())) {
            Question existing = questionRepository.findById(question.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));
            PropertyCopyUtil.copyNonNullProperties(question, existing);
            questionRepository.save(existing);
            return existing.getQuestionId().toString();
        } else {
            Question newQuestion = new Question();
            newQuestion.setQuestion(question.getQuestion());
            newQuestion.setOptions(question.getOptions());
            newQuestion.setCorrectAnswer(question.getCorrectAnswer());
            newQuestion.setSolution(question.getSolution());
            newQuestion.setImage(question.getImage());
            newQuestion.setDifficulty(question.getDifficulty());

            questionRepository.save(newQuestion);
            return newQuestion.getQuestionId().toString();
        }
    }

    public ApiResponse<?> deleteQuestion(String questionId) {
        if (!questionRepository.existsById(new ObjectId(questionId))) {
            return ApiResponse.error(HttpStatus.BAD_REQUEST, "Question not found");
        }

        // Remove from all quizzes
        List<Quiz> quizzes = quizRepository.findAll();
        for (Quiz quiz : quizzes) {
            if (quiz.getQuizContent().removeIf(qid -> qid.equals(questionId))) {
                quizRepository.save(quiz);
            }
        }

        questionRepository.deleteById(new ObjectId(questionId));
        return ApiResponse.success("Question deleted and removed from all quizzes");
    }
}

