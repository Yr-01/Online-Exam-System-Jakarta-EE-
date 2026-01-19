package com.example.quickexam.repository;

import com.example.quickexam.entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@Stateless
@Transactional
public class ExamDAO {
    @PersistenceContext
    private EntityManager em;

    public List<Exam> getMyExams(User user){
        try {
            return em.createQuery("select ex from Exam ex where ex.createdBy = :user", Exam.class)
                    .setParameter("user",user)
                    .getResultList();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Long countUnconfirmedUsers(Long examId) {
        try {
            return em.createQuery(
                            "SELECT COUNT(ce.candidate.userId) " +
                                    "FROM CandidateExam ce " +
                                    "WHERE ce.exam.examId = :examId " +
                                    "and ce.candidateExamId not in (" +
                                    "select cs.candidateExam.candidateExamId " +
                                    "from CodeSession cs)",
                            Long.class)
                    .setParameter("examId", examId)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    public Long countConfirmedUsers(Long examId) {
        try {
            return em.createQuery(
                            "SELECT COUNT(ce.candidate.userId) " +
                                    "FROM CandidateExam ce " +
                                    "WHERE ce.exam.examId = :examId " +
                                    "and ce.candidateExamId in (" +
                                    "select cs.candidateExam.candidateExamId " +
                                    "from CodeSession cs)",
                            Long.class)
                    .setParameter("examId", examId)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    public Exam getExamById(Long examId) {
        try {
            return em.find(Exam.class, examId);
        }catch (Exception e){
            return null;
        }
    }
    public Long countUsersWhoPassed(Long examId) {
        try {
            return em.createQuery(
                            "SELECT COUNT(distinct ca.candidate.userId) FROM CandidateAnswer ca WHERE ca.exam.examId = :examId", Long.class)
                    .setParameter("examId", examId)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public List<Question> getQuestions(Long examId) {
        try {
            return em.createQuery(
                            "SELECT qt FROM Question qt WHERE qt.exam.examId = :examId", Question.class)
                    .setParameter("examId", examId)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<QuestionOption> getQuestionOptions(Long questionId) {
        try {
            return em.createQuery(
                            "SELECT qt FROM QuestionOption qt WHERE qt.question.questionId = :questionId", QuestionOption.class)
                    .setParameter("questionId", questionId)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Exam saveExam(Exam exam) {
        try {
            if (exam.getExamId() == null) {
                em.persist(exam);
                em.flush();
                return exam;
            } else {
                Exam merged = em.merge(exam);
                em.flush();
                return merged;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save exam: " + e.getMessage(), e);
        }
    }

    public void deleteExam(Exam exam) {
        try {
            Long examId = exam.getExamId();

            if (examId == null) {
                System.out.println("Cannot delete exam with null ID");
                return;
            }

            System.out.println("Starting delete process for exam ID: " + examId);

            int scoresDeleted = em.createQuery("DELETE FROM CandidateExamScore ces WHERE ces.exam.examId = :examId")
                    .setParameter("examId", examId)
                    .executeUpdate();
            System.out.println("Deleted " + scoresDeleted + " CandidateExamScores");

            int answersDeleted = em.createQuery("DELETE FROM CandidateAnswer ca WHERE ca.exam.examId = :examId")
                    .setParameter("examId", examId)
                    .executeUpdate();
            System.out.println("Deleted " + answersDeleted + " CandidateAnswers");

            int sessionsDeleted = em.createQuery("DELETE FROM CodeSession cs WHERE cs.exam.examId = :examId")
                    .setParameter("examId", examId)
                    .executeUpdate();
            System.out.println("Deleted " + sessionsDeleted + " CodeSessions");

            int candidateExamsDeleted = em.createQuery("DELETE FROM CandidateExam ce WHERE ce.exam.examId = :examId")
                    .setParameter("examId", examId)
                    .executeUpdate();
            System.out.println("Deleted " + candidateExamsDeleted + " CandidateExams");

            int optionsDeleted = em.createQuery("DELETE FROM QuestionOption qo WHERE qo.question.exam.examId = :examId")
                    .setParameter("examId", examId)
                    .executeUpdate();
            System.out.println("Deleted " + optionsDeleted + " QuestionOptions");

            int questionsDeleted = em.createQuery("DELETE FROM Question q WHERE q.exam.examId = :examId")
                    .setParameter("examId", examId)
                    .executeUpdate();
            System.out.println("Deleted " + questionsDeleted + " Questions");

            Exam examToDelete = em.find(Exam.class, examId);
            if (examToDelete != null) {
                em.remove(examToDelete);
                System.out.println("Exam entity removed from persistence context");
            } else {
                System.out.println("Exam not found in database, may have been deleted already");
            }

            em.flush();

            System.out.println("Successfully deleted exam with ID: " + examId);

        } catch (Exception e) {
            System.err.println("Error deleting exam with ID: " + exam.getExamId());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete exam with ID: " + exam.getExamId(), e);
        }
    }

    public List<Object[]> getUnconfirmedCandidatesForExam(Long examId) {
        try {
            return em.createQuery(
                            "SELECT ce.candidate.userId, CONCAT(ce.candidate.nom, ' ', ce.candidate.prenom) as fullname, ce.candidate.email, ce.date, ce.heureDebut, ce.heureFin " +
                                    "FROM CandidateExam ce " +
                                    "WHERE ce.exam.examId = :examId " +
                                    "AND ce.candidateExamId NOT IN (" +
                                    "   SELECT cs.candidateExam.candidateExamId FROM CodeSession cs where cs.exam.examId = :examId" +
                                    ")"+
                            "order by ce.date desc, ce.heureDebut desc",
                            Object[].class
                    )
                    .setParameter("examId", examId)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<Object[]> getConfirmedCandidatesForExam(Long examId) {
        try {
            return em.createQuery(
                            "SELECT cs.code, ce.candidate.userId, " +
                                    "       CONCAT(ce.candidate.nom, ' ', ce.candidate.prenom) AS fullname, " +
                                    "       ce.candidate.email, ce.date, ce.heureDebut, ce.heureFin " +
                                    "FROM CandidateExam ce " +
                                    "INNER JOIN CodeSession cs ON cs.candidateExam.candidateExamId = ce.candidateExamId AND cs.exam.examId = :examId " +
                                    "WHERE ce.exam.examId = :examId "+
                                    "order by ce.date desc, ce.heureDebut desc",
                            Object[].class
                    )
                    .setParameter("examId", examId)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }
    public List<Object[]> getPassedCandidatesForExam(Long examId) {
        try {
            return em.createQuery(
                            "SELECT cs.code, ce.candidate.userId, " +
                                    "       CONCAT(ce.candidate.nom, ' ', ce.candidate.prenom) AS fullname, " +
                                    "       ce.candidate.email, ce.date, ce.heureDebut, ce.heureFin, ces.score, ce.exam.nbrQuestion " +
                                    "FROM CandidateExam ce " +
                                    "INNER JOIN CodeSession cs ON cs.candidate = ce.candidate AND cs.exam.examId = :examId " +
                                    "INNER JOIN CandidateExamScore ces ON ces.candidate = ce.candidate AND ces.exam.examId = :examId " +
                                    "WHERE ce.exam.examId = :examId "+
                                    "AND ce.candidate IN (" +
                                    "   SELECT ca.candidate FROM CandidateAnswer ca where ca.exam.examId = :examId " +
                                    ")"+
                                    "GROUP BY ce.candidate.userId, ce.candidate.nom, ce.candidate.prenom, ce.candidate.email, ce.exam.nbrQuestion "+
                                    "order by ce.date desc, ce.heureDebut desc",
                            Object[].class)
                    .setParameter("examId", examId)
                    .getResultList();

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    public Long checkIfCodeExists(String code) {
        try {
            return em.createQuery(
                            "SELECT COUNT(cs) FROM CodeSession cs WHERE cs.code = :code",
                            Long.class)
                    .setParameter("code", code)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    public void setCodeSession(CodeSession codeSession) {
        em.persist(codeSession);
    }
    public List<Object[]> getAllExams() {
        try {
            return em.createQuery(
                            "SELECT e.examId, e.theme, e.typeQuestion, e.nbrQuestion, e.examTime, case " +
                                                                                    "when e.createdBy.role = 'ADMINISTRATOR' then 'QuickExam'" +
                                                                                    "else concat(e.createdBy.nom, ' ',e.createdBy.prenom) " +
                                                                                    "end as createdBy " +
                                    "FROM Exam e",
                            Object[].class)
                    .getResultList();

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    public void setCandidateExam(CandidateExam candidateExam) {
        em.persist(candidateExam);
    }
    public void updateCodeSession(CodeSession codeSession) {
        em.merge(codeSession);
    }
    public CodeSession getCandiateCodeSession(Long candidateId, String code) {
        try {
            return em.createQuery("select cs from CodeSession cs where cs.candidate.userId = :candidateId and cs.code =:code",CodeSession.class)
                    .setParameter("candidateId",candidateId)
                    .setParameter("code",code)
                    .getSingleResult();
        }catch (Exception e){
            return null;
        }
    }
    public CodeSession getCodeSessionWithId(Long codeSessionId) {
        try {
            return em.find(CodeSession.class, codeSessionId);
        }catch (Exception e){
            return null;
        }
    }
    public void saveCandidateAnswer(CandidateAnswer candidateAnswer) {
        em.persist(candidateAnswer);
    }
    public void saveCandidateExamScore(CandidateExamScore candidateExamScore) {
        em.persist(candidateExamScore);
    }
    public List<Object[]> getExamScores(Long candidateId) {
        try {
            return em.createQuery("select ces.exam.theme, ces.exam.nbrQuestion, ces.exam.examTime, ce.heureDebut, ce.date, ces.finishedAt, "+
                                    "case "+
                                    "when e.createdBy.role = 'ADMINISTRATOR' then 'QuickExam' "+
                                    "else concat(e.createdBy.nom, ' ',e.createdBy.prenom) " +
                                    "end as createdBy,"+
                                    "ces.score" +
                            " from CandidateExamScore ces " +
                            "inner join CandidateExam ce on ce.exam.examId = ces.exam.examId and ce.candidate.userId =:candidateId " +
                            "inner join Exam e on e.examId = ces.exam.examId" +
                            " where ces.candidate.userId =:candidateId " +
                            "group by ce.candidateExamId "+
                            "order by ces.finishedAt desc"
                            ,Object[].class)
                    .setParameter("candidateId",candidateId)
                    .getResultList();
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
    public CandidateExam getCandidateExam(Long candidateId, Long examId) {
        try {
            return em.createQuery("SELECT ce FROM CandidateExam ce WHERE ce.candidate.userId = :candidateId AND ce.exam.examId = :examId order by ce.createdAt desc ", CandidateExam.class)
                    .setParameter("candidateId", candidateId)
                    .setParameter("examId", examId)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    public CandidateExam getCandidateExamWithId(Long candidateExamId) {
        try {
            return em.find(CandidateExam.class, candidateExamId);
        }catch (Exception e){
            return null;
        }
    }
    public void updateCodeSessionWithId(Long id){
        em.createQuery("update CodeSession set used = true where codeSessionId = :id")
                .setParameter("id",id)
                .executeUpdate();
    }

    public List<Exam> MyLatestExams(User user){
        return em.createQuery("select e from Exam e where e.createdBy = :user ",Exam.class)
                .setParameter("user",user)
                .setMaxResults(6)
                .getResultList();
    }
    public Long getTotalRejectedCreators(){
        return em.createQuery("select count(cr) from CreatorRequest cr where cr.status = :status", Long.class)
                .setParameter("status",RequestStatus.REJECTED)
                .getSingleResult();
    }
    public Long getTotalPendingCreators(){
        return em.createQuery("select count(cr) from CreatorRequest cr where cr.status = :status", Long.class)
                .setParameter("status",RequestStatus.PENDING)
                .getSingleResult();
    }
    public Long getTotalCreator(){
        return em.createQuery("select count(cr) from CreatorRequest cr where cr.status = :status", Long.class)
                .setParameter("status",RequestStatus.APPROVED)
                .getSingleResult();
    }
    public Long getTotalCandidate(){
        return em.createQuery("select count(u) from User u where u.role = :role", Long.class)
                .setParameter("role",UserRole.CANDIDATE)
                .getSingleResult();
    }
    public Long getTotalMyExams(User user){
        return em.createQuery("select count(e) from Exam e where e.createdBy = :user", Long.class)
                .setParameter("user",user)
                .getSingleResult();
    }
    public Long getUsersRegisteredThisMonth() {
        return em.createQuery(
                        "SELECT COUNT(u) FROM User u " +
                                "WHERE YEAR(u.createdAt) = YEAR(CURRENT_DATE) " +
                                "AND MONTH(u.createdAt) = MONTH(CURRENT_DATE)", Long.class)
                .getSingleResult();
    }
    public Long getExamsCreatedThisMonth() {
        return em.createQuery(
                        "SELECT COUNT(e) FROM Exam e " +
                                "WHERE YEAR(e.createdAt) = YEAR(CURRENT_DATE) " +
                                "AND MONTH(e.createdAt) = MONTH(CURRENT_DATE)", Long.class)
                .getSingleResult();
    }
    public Double getAverageScoreOverall() {
        return em.createQuery("SELECT AVG(s.score) FROM CandidateExamScore s", Double.class)
                .getSingleResult();
    }
    public Long getActiveExamsToday() {
        return em.createQuery("SELECT COUNT(ce) FROM CandidateExam ce WHERE ce.date = CURRENT_DATE", Long.class)
                .getSingleResult();
    }
    public Long getTotalExams() {
        return em.createQuery("SELECT COUNT(e) FROM Exam e", Long.class).getSingleResult();
    }

    public Long getTotalCompletedExams() {
        return em.createQuery("SELECT COUNT(s) FROM CandidateExamScore s", Long.class).getSingleResult();
    }

    public Long getTotalScheduledExams() {
        return em.createQuery("SELECT COUNT(ce) FROM CandidateExam ce", Long.class).getSingleResult();
    }

    public List<Exam> findAllExamsOrderedByCreatedAtDesc(int limit) {
        return em.createQuery("SELECT e FROM Exam e ORDER BY e.createdAt DESC", Exam.class)
                .setMaxResults(limit)
                .getResultList();
    }
    public List<CreatorRequest> getPendingCreatorRequestsList(int limit) {
        return em.createQuery("SELECT cr FROM CreatorRequest cr JOIN FETCH cr.user WHERE cr.status = :status ORDER BY cr.createdAt DESC", CreatorRequest.class)
                .setParameter("status", RequestStatus.PENDING)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Exam> findMyRecentExams(User creator, int limit) {
        return em.createQuery(
                        "SELECT e FROM Exam e WHERE e.createdBy = :creator ORDER BY e.createdAt DESC", Exam.class)
                .setParameter("creator", creator)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<CandidateExam> findRecentCandidateExamsByCreator(User creator, int limit) {
        return em.createQuery(
                        "SELECT ce FROM CandidateExam ce WHERE ce.exam.createdBy = :creator ORDER BY ce.date DESC, ce.heureDebut DESC", CandidateExam.class)
                .setParameter("creator", creator)
                .setMaxResults(limit)
                .getResultList();
    }

    public Long countTotalCandidatesInMyExams(User creator) {
        return em.createQuery(
                        "SELECT COUNT(ce) FROM CandidateExam ce WHERE ce.exam.createdBy = :creator", Long.class)
                .setParameter("creator", creator)
                .getSingleResult();
    }

    public Long countCompletedExamsByCreator(User creator) {
        return em.createQuery(
                        "SELECT COUNT(s) FROM CandidateExamScore s WHERE s.exam.createdBy = :creator", Long.class)
                .setParameter("creator", creator)
                .getSingleResult();
    }

    public Long countPassedCandidatesInMyExams(User creator) {
        return em.createQuery(
                        "SELECT COUNT(s) FROM CandidateExamScore s " +
                                "WHERE s.exam.createdBy = :creator ", Long.class)
                .setParameter("creator", creator)
                .getSingleResult();
    }

}