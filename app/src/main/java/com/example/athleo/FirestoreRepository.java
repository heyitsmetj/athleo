package com.example.athleo;

import com.example.athleo.models.*;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirestoreRepository {
    private static FirestoreRepository instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private final Gson gson;

    public FirebaseFirestore getDb() { return db; }

    private FirestoreRepository() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        gson = new Gson();
    }

    public static synchronized FirestoreRepository getInstance() {
        if (instance == null) {
            instance = new FirestoreRepository();
        }
        return instance;
    }

    public Task<Void> seedMockData() {
        com.google.firebase.firestore.WriteBatch batch = db.batch();
        
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            String email = mAuth.getCurrentUser().getEmail();

            // 1. Create User Profile
            User user = new User(uid, "Academy Member", email, "Student");
            user.setRank("Elite");
            user.setAttendancePercentage(92.5);
            user.setPerformanceScore(88);
            user.setProfileImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuDbmFQZ9RetrDl3EfSkGrBT2Ws_Hdn7jrxS8nyQTvLXJ8vZ5KbQ1Z0T5LwBoekPNondSbNEUul4404RiR-gyF-wHOUQYzAejfejxZk1WWRf0TXwiZfKhWau6fr4DcSlDfKmZMY9FkK2FvtKyNnMlNl8jYVow6ceH9zPR7CXlK_eByJTHzBjtADdaDUdBMGP8oYQko6BIfKSF6gQ5SfF8XL0f_uF7_4Hd9T0idBSzh-bMStfAZZx8WGkR1IMrZf6B3UxENxCj5j3hljN");
            
            batch.set(db.collection("Users").document(uid), user);
        }

        // 2. Sample Announcements
        String[] annTitles = {"Academy Season Kickoff", "New Training Kits", "System Maintenance"};
        for (int i = 0; i < annTitles.length; i++) {
            Announcement a = new Announcement("ann_" + i, annTitles[i], "Important update regarding the " + annTitles[i].toLowerCase() + ".", "Admin", System.currentTimeMillis());
            batch.set(db.collection("Announcements").document("ann_" + i), a);
        }

        // 3. Sample Schedules
        for (int i = 1; i <= 3; i++) {
            MatchSchedule s = new MatchSchedule("sched_" + i, "League Match " + i, "Oct " + (20+i), "10:00 AM", "Main Stadium", "Opponent Team " + i);
            batch.set(db.collection("MatchSchedules").document("sched_" + i), s);
        }

        // 4. Sample Players
        for (int i = 1; i <= 5; i++) {
            User p = new User("player_" + i, "Academy Player " + i, "player" + i + "@athleo.com", "Student");
            p.setRank("Pro");
            p.setPerformanceScore(70 + (i * 5));
            p.setProfileImageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuBQDZSxZ08cVSCpoChW0R8nlOANHJxbM0UoZwv3pAiru9gRBtG0IhSr9GeOnrJXjpPSx-PAp2JKonMNYjn9_W2H2XJ3Y16v2l3LhFsewgrxwVMkPoMaYO64DvNN96R4_bTGjNTiFrWiPC1nfbWHv0lj4IEFJ6kJrK1U_LQD8IjlH-oL7VAsAvPHFGNMUogogsElQDJ8giBvhgZN_4_VNlPFPsEJqgjU04twkjH9TB-HZQ8gcRFGB076V6LQBTrbwcDmInzs6XMzxgmInzs6XMzxgmI");
            batch.set(db.collection("Users").document("player_" + i), p);
        }

        // 4b. Sample Trainers
        String[] trainerNames = {"Coach Rajan", "Coach Sanjay", "Amit Trainer"};
        String[] trainerRoles = {"Head Trainer", "Physical Coach", "Junior Trainer"};
        for (int i = 0; i < trainerNames.length; i++) {
            User t = new User("trainer_" + i, trainerNames[i], "trainer" + i + "@athleo.com", "Trainer");
            t.setRank(trainerRoles[i]); 
            batch.set(db.collection("Users").document("trainer_" + i), t);
        }

        // 5. Sample Financials
        com.example.athleo.models.Expense overview = new com.example.athleo.models.Expense("overview", "Total Operating Balance", 48295000, "Balance", System.currentTimeMillis());
        overview.setStatus("approved");
        batch.set(db.collection("Financials").document("overview"), overview);

        com.example.athleo.models.Expense e1 = new com.example.athleo.models.Expense("exp_1", "Training Equipment", 25000, "Equipment", System.currentTimeMillis() - 86400000);
        e1.setStatus("pending");
        batch.set(db.collection("Financials").document("exp_1"), e1);

        com.example.athleo.models.Expense e2 = new com.example.athleo.models.Expense("exp_2", "Travel Allowance", 15000, "Travel", System.currentTimeMillis() - 172800000);
        e2.setStatus("pending");
        batch.set(db.collection("Financials").document("exp_2"), e2);

        // 6. Sample Tasks
        String[] taskTitles = {"Tactical Review", "Agility Drill", "Video Analysis"};
        for (int i = 0; i < taskTitles.length; i++) {
            com.example.athleo.models.TaskItem t = new com.example.athleo.models.TaskItem("task_" + i, taskTitles[i], "Complete the " + taskTitles[i].toLowerCase() + " by Friday.", "Active", 60 + (i * 10));
            batch.set(db.collection("Tasks").document("task_" + i), t);
        }

        // 7. Sample FAQ
        String[] questions = {"How to reset password?", "Where is the match schedule?", "How to track progress?"};
        for (int i = 0; i < questions.length; i++) {
            com.example.athleo.models.FaqItem f = new com.example.athleo.models.FaqItem("faq_" + i, questions[i], "You can find this in the support section of your dashboard.");
            batch.set(db.collection("FAQ").document("faq_" + i), f);
        }

        // 8. Sample Chat Groups
        ChatGroup allGroup = new ChatGroup("group_all", "Academy All", "default_all");
        batch.set(db.collection("ChatGroups").document("group_all"), allGroup);

        ChatGroup staffGroup = new ChatGroup("group_staff", "Staff Comms", "default_staff");
        batch.set(db.collection("ChatGroups").document("group_staff"), staffGroup);

        return batch.commit();
    }

    public Task<String> getUserDataJson() {
        return db.collection("Users").document(mAuth.getCurrentUser().getUid()).get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        User user = task.getResult().toObject(User.class);
                        if (user != null) {
                            user.setId(task.getResult().getId());
                        }
                        return gson.toJson(user);
                    }
                    return "{}";
                });
    }

    public Task<String> getAllUsersJson() {
        return db.collection("Users").get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        java.util.List<User> users = task.getResult().toObjects(User.class);
                        for (int i = 0; i < users.size(); i++) {
                            users.get(i).setId(task.getResult().getDocuments().get(i).getId());
                        }
                        return gson.toJson(users);
                    }
                    return "[]";
                });
    }

    public Task<Void> createChatGroup(String name, java.util.List<String> memberIds, String adminId) {
        String id = db.collection("ChatGroups").document().getId();
        ChatGroup group = new ChatGroup(id, name, "custom");
        group.getAdminIds().add(adminId);
        group.getMemberIds().addAll(memberIds);
        return db.collection("ChatGroups").document(id).set(group);
    }

    public Task<String> getChatGroupJson(String groupId) {
        return db.collection("ChatGroups").document(groupId).get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return gson.toJson(task.getResult().toObject(ChatGroup.class));
                    }
                    return "{}";
                });
    }

    public Task<Void> updateChatGroupMembers(String groupId, java.util.List<String> memberIds, java.util.List<String> adminIds) {
        return db.collection("ChatGroups").document(groupId)
                .update("memberIds", memberIds, "adminIds", adminIds);
    }

    public Task<String> getAnnouncementsJson() {
        return db.collection("Announcements").orderBy("timestamp").get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return gson.toJson(task.getResult().toObjects(Announcement.class));
                    }
                    return "[]";
                });
    }

    public Task<String> getSchedulesJson() {
        return db.collection("MatchSchedules").get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return gson.toJson(task.getResult().toObjects(MatchSchedule.class));
                    }
                    return "[]";
                });
    }

    public Task<String> getFinancialsJson() {
        return db.collection("Financials").get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return gson.toJson(task.getResult().toObjects(com.example.athleo.models.Expense.class)); // Using Expense for all financial records
                    }
                    return "[]";
                });
    }

    public Task<String> getPlayersJson() {
        return db.collection("Users").whereEqualTo("role", "Student").get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        java.util.List<User> users = new java.util.ArrayList<>();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : task.getResult().getDocuments()) {
                            User u = doc.toObject(User.class);
                            if (u != null) {
                                u.setId(doc.getId());
                                users.add(u);
                            }
                        }
                        return gson.toJson(users);
                    }
                    return "[]";
                });
    }

    public Task<String> getTasksJson() {
        return db.collection("Tasks").get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        java.util.List<com.example.athleo.models.TaskItem> items = new java.util.ArrayList<>();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : task.getResult().getDocuments()) {
                            com.example.athleo.models.TaskItem item = doc.toObject(com.example.athleo.models.TaskItem.class);
                            if (item != null) {
                                item.setId(doc.getId());
                                items.add(item);
                            }
                        }
                        return gson.toJson(items);
                    }
                    return "[]";
                });
    }

    public Task<String> getFaqJson() {
        return db.collection("FAQ").get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return gson.toJson(task.getResult().toObjects(com.example.athleo.models.FaqItem.class));
                    }
                    return "[]";
                });
    }

    public Task<Void> createTask(String title, String description, String category, String deadline) {
        return createTaskForPlayer(title, description, category, "All", deadline);
    }

    public Task<Void> createTaskForPlayer(String title, String description, String category, String studentId, String deadline) {
        String id = db.collection("Tasks").document().getId();
        com.example.athleo.models.TaskItem task = new com.example.athleo.models.TaskItem(id, title, description, "Active", 0);
        task.setCategory(category);
        task.setAssignedTo(studentId);
        task.setTimestamp(System.currentTimeMillis());
        task.setDeadline(deadline);
        return db.collection("Tasks").document(id).set(task);
    }

    public Task<Void> completeTask(String taskId, String proofUrl) {
        return db.collection("Tasks").document(taskId)
                .update("status", "Pending Review", "submissionUrl", proofUrl);
    }

    public Task<Void> evaluateTask(String taskId, String status, int score, String feedback) {
        return db.collection("Tasks").document(taskId)
                .update("status", status, "score", score, "coachFeedback", feedback);
    }

    public Task<Void> deleteTask(String taskId) {
        return db.collection("Tasks").document(taskId).delete();
    }

    public Task<Void> submitPerformance(String playerId, String rating, String feedback) {
        String logId = db.collection("Users").document(playerId).collection("PerformanceHistory").document().getId();
        java.util.Map<String, Object> log = new java.util.HashMap<>();
        log.put("id", logId);
        log.put("playerId", playerId);
        log.put("score", Double.parseDouble(rating));
        log.put("feedback", feedback);
        log.put("timestamp", System.currentTimeMillis());

        db.collection("Users").document(playerId).collection("PerformanceHistory").document(logId).set(log);

        return db.collection("Users").document(playerId)
                .update("performanceScore", Double.parseDouble(rating), "coachFeedback", feedback);
    }

    public Task<Void> updatePerformanceScore(String userId, double newScore) {
        return db.collection("Users").document(userId).update("performanceScore", newScore);
    }

    public Task<Void> updateUserProfile(String userId, String name, String profileImageUrl) {
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            return db.collection("Users").document(userId).update("name", name, "profileImageUrl", profileImageUrl);
        } else {
            return db.collection("Users").document(userId).update("name", name);
        }
    }

    public Task<String> getPerformanceHistoryJson(String playerId) {
        return db.collection("Users").document(playerId).collection("PerformanceHistory")
                .orderBy("timestamp").get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : task.getResult()) {
                            list.add(doc.getData());
                        }
                        return gson.toJson(list);
                    }
                    return "[]";
                });
    }

    public Task<String> getUserJsonById(String userId) {
        return db.collection("Users").document(userId).get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        User user = task.getResult().toObject(User.class);
                        if(user != null) {
                            user.setId(task.getResult().getId());
                            return gson.toJson(user);
                        }
                    }
                    return "{}";
                });
    }

    public Task<Void> updateExpenseStatus(String expenseId, String status) {
        return db.collection("Financials").document(expenseId)
                .update("status", status);
    }

    public Task<Void> createAnnouncement(String title, String message, String type) {
        String id = db.collection("Announcements").document().getId();
        Announcement ann = new Announcement(id, title, message, "Academy Admin", System.currentTimeMillis());
        return db.collection("Announcements").document(id).set(ann);
    }

    public Task<String> getStaffJson() {
        return db.collection("Users").whereEqualTo("role", "Trainer").get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return gson.toJson(task.getResult().toObjects(User.class));
                    }
                    return "[]";
                });
    }

    public Task<String> getChatGroupsJson() {
        return db.collection("ChatGroups").get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return gson.toJson(task.getResult().toObjects(ChatGroup.class));
                    }
                    return "[]";
                });
    }

    public Task<String> getChatMessagesJson(String groupId) {
        return db.collection("ChatGroups").document(groupId).collection("Messages")
                .orderBy("timestamp").get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        return gson.toJson(task.getResult().toObjects(ChatMessage.class));
                    }
                    return "[]";
                });
    }

    public Task<Void> sendChatMessage(String groupId, String senderId, String senderName, String role, String text) {
        String msgId = db.collection("ChatGroups").document(groupId).collection("Messages").document().getId();
        ChatMessage msg = new ChatMessage(msgId, senderId, senderName, role, text, System.currentTimeMillis());
        
        // Update the group's last activity
        db.collection("ChatGroups").document(groupId).update("createdAt", System.currentTimeMillis());
        
        return db.collection("ChatGroups").document(groupId).collection("Messages").document(msgId).set(msg);
    }
}
