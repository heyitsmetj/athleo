package com.example.athleo;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.example.athleo.models.User;
import com.example.athleo.models.Expense;

import android.webkit.WebView;
import android.webkit.ValueCallback;
import android.net.Uri;
import android.content.ActivityNotFoundException;

public class WebAppInterface {
    Context mContext;
    WebView webView;

    private ValueCallback<Uri[]> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;

    WebAppInterface(Context c, WebView v) {
        mContext = c;
        webView = v;
    }

    private void runOnWebViewThread(Runnable runnable) {
        if (webView != null) {
            webView.post(runnable);
        }
    }

    private void safeEval(String script) {
        runOnWebViewThread(() -> webView.evaluateJavascript(script, null));
    }

    private String safeJson(String json) {
        if (json == null) return "null";
        return new Gson().toJson(json);
    }

    private void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    // AUTH METHODS
    @JavascriptInterface
    public void login(String email, String password) {
        if (mContext instanceof LoginActivity) {
            ((LoginActivity) mContext).loginFromWeb(email, password);
        } else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(intent);
                    } else {
                        showToast("Login Failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown Error"));
                        safeEval("if(window.loginError) window.loginError('" + (task.getException() != null ? task.getException().getMessage().replace("'", "\\'") : "Auth Error") + "');");
                    }
                });
        }
    }

    @JavascriptInterface
    public void registerUser(String email, String password, String name, String role) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(res -> {
                String uid = res.getUser().getUid();
                User newUser = new User(uid, name, email, role);
                FirestoreRepository.getInstance().getDb().collection("Users").document(uid).set(newUser)
                    .addOnSuccessListener(aVoid -> {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        mContext.startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        showToast("Profile Setup Failed: " + e.getMessage());
                        safeEval("if(window.loginError) window.loginError('Firestore Error: " + e.getMessage().replace("'", "\\'") + "');");
                    });
            })
            .addOnFailureListener(e -> {
                showToast("Registration Failed: " + e.getMessage());
                safeEval("if(window.loginError) window.loginError('Auth Error: " + e.getMessage().replace("'", "\\'") + "');");
            });
    }

    @JavascriptInterface
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        if (mContext instanceof android.app.Activity) {
            ((android.app.Activity) mContext).finish();
        }
    }

    // NAVIGATION METHODS
    @JavascriptInterface
    public void openAnnouncements() { mContext.startActivity(new Intent(mContext, AnnouncementsActivity.class)); }
    @JavascriptInterface
    public void openCommunity() { mContext.startActivity(new Intent(mContext, ChatListActivity.class)); }
    @JavascriptInterface
    public void openFeedback() { mContext.startActivity(new Intent(mContext, StudentFeedbackActivity.class)); }
    @JavascriptInterface
    public void openSchedules() { mContext.startActivity(new Intent(mContext, MatchSchedulesActivity.class)); }
    @JavascriptInterface
    public void openTaskManagement() { mContext.startActivity(new Intent(mContext, TaskManagementActivity.class)); }
    @JavascriptInterface
    public void openAttendance() { mContext.startActivity(new Intent(mContext, MarkAttendanceActivity.class)); }
    @JavascriptInterface
    public void openManageExpenses() { mContext.startActivity(new Intent(mContext, ManageExpensesActivity.class)); }
    @JavascriptInterface
    public void openEmployeePayments() { mContext.startActivity(new Intent(mContext, ManageEmployeePaymentsActivity.class)); }
    @JavascriptInterface
    public void openManageTrainers() { mContext.startActivity(new Intent(mContext, ManageTrainersActivity.class)); }
    @JavascriptInterface
    public void openManageFees() { mContext.startActivity(new Intent(mContext, ManageFeesActivity.class)); }
    @JavascriptInterface
    public void editProfile() { mContext.startActivity(new Intent(mContext, EditProfileActivity.class)); }
    @JavascriptInterface
    public void openProfile() { editProfile(); }
    @JavascriptInterface
    public void showAddAnnouncement() { mContext.startActivity(new Intent(mContext, CreateAnnouncementActivity.class)); }
    @JavascriptInterface
    public void showAddTask() { mContext.startActivity(new Intent(mContext, CreateTaskActivity.class)); }

    @JavascriptInterface
    public void openPerformanceReports() {
        FirestoreRepository.getInstance().getUserDataJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = new Gson().fromJson(task.getResult(), User.class);
                if (user != null && "Trainer".equals(user.getRole())) {
                    mContext.startActivity(new Intent(mContext, PerformanceEntryActivity.class));
                } else {
                    mContext.startActivity(new Intent(mContext, PerformanceReportsActivity.class));
                }
            }
        });
    }

    // DATA LOADING METHODS (WEBVIEW COMPATIBLE)
    @JavascriptInterface
    public void loadUserProfile() {
        FirestoreRepository.getInstance().getUserDataJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String json = task.getResult();
                safeEval("if(window.renderProfile) { var d = " + safeJson(json) + "; renderProfile(typeof d === 'string' ? JSON.parse(d) : d); }");
                
                // Also update student count if applicable
                FirestoreRepository.getInstance().getDb().collection("Users").whereEqualTo("role", "Student").get().addOnCompleteListener(countTask -> {
                    if (countTask.isSuccessful() && countTask.getResult() != null) {
                        int count = countTask.getResult().size();
                        String js = "var els = document.querySelectorAll('p'); for(var i=0; i<els.length; i++){ if(els[i].innerText.trim().toUpperCase() === 'TOTAL STUDENTS' && els[i].nextElementSibling && els[i].nextElementSibling.querySelector('p')) { els[i].nextElementSibling.querySelector('p').innerText = '" + count + "'; } }";
                        safeEval(js);
                    }
                });
            }
        });
    }

    @JavascriptInterface
    public void loadRole() {
        FirestoreRepository.getInstance().getUserDataJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = new Gson().fromJson(task.getResult(), User.class);
                if (user != null && user.getRole() != null) {
                    safeEval("if(window.receiveRole) receiveRole('" + user.getRole() + "');");
                }
            }
        });
    }

    @JavascriptInterface
    public void loadAnnouncements() {
        FirestoreRepository.getInstance().getAnnouncementsJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String json = task.getResult();
                safeEval("if(window.renderAnnouncements) { var d = " + safeJson(json) + "; window.renderAnnouncements(typeof d === 'string' ? JSON.parse(d) : d); }");
            }
        });
    }

    @JavascriptInterface
    public void loadSchedules() {
        FirestoreRepository.getInstance().getSchedulesJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String json = task.getResult();
                safeEval("if(window.renderSchedules) { var d = " + safeJson(json) + "; window.renderSchedules(typeof d === 'string' ? JSON.parse(d) : d); }");
            }
        });
    }

    @JavascriptInterface
    public void loadFinancials() {
        FirestoreRepository.getInstance().getFinancialsJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String json = task.getResult();
                safeEval("if(window.renderFinancials) { var d = " + safeJson(json) + "; window.renderFinancials(typeof d === 'string' ? JSON.parse(d) : d); }");
            }
        });
    }

    @JavascriptInterface
    public void loadPlayers() {
        FirestoreRepository.getInstance().getPlayersJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String json = task.getResult();
                safeEval("if(window.renderPlayers) { var d = " + safeJson(json) + "; window.renderPlayers(typeof d === 'string' ? JSON.parse(d) : d); }");
            }
        });
    }

    @JavascriptInterface
    public void loadAttendanceList() {
        FirestoreRepository.getInstance().getPlayersJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String json = task.getResult();
                safeEval("if(window.renderAttendanceList) { var d = " + safeJson(json) + "; window.renderAttendanceList(typeof d === 'string' ? JSON.parse(d) : d); }");
            }
        });
    }

    @JavascriptInterface
    public void markAttendance(String studentId, boolean present) {
        double percentageDelta = present ? 0.5 : -0.5; // Dummy logic to update percentage
        FirestoreRepository.getInstance().getDb().collection("Users").document(studentId).get().addOnSuccessListener(doc -> {
            User u = doc.toObject(User.class);
            if(u != null) {
                double newPct = Math.max(0, Math.min(100, u.getAttendancePercentage() + percentageDelta));
                FirestoreRepository.getInstance().getDb().collection("Users").document(studentId).update("attendancePercentage", newPct);
            }
        });
    }

    @JavascriptInterface
    public void submitAttendance() {
        showToast("Attendance saved for today.");
    }

    @JavascriptInterface
    public void openSubmitExpense() {
        Intent intent = new Intent(mContext, SubmitExpenseActivity.class);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void submitExpense(String title, double amount, String category, String notes) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        FirestoreRepository.getInstance().getDb().collection("Users").document(uid).get()
            .addOnSuccessListener(doc -> {
                String name = doc.exists() ? doc.getString("name") : "Staff";
                Expense expense = new Expense();
                expense.setDescription(title);
                expense.setAmount(amount);
                expense.setCategory(category);
                expense.setNotes(notes);
                expense.setTimestamp(System.currentTimeMillis());
                expense.setStatus("Pending");
                expense.setSubmittedBy(name);
                expense.setSubmitterId(uid);

                com.google.firebase.firestore.DocumentReference ref = 
                    FirestoreRepository.getInstance().getDb().collection("Financials").document();
                expense.setId(ref.getId());

                ref.set(expense)
                    .addOnSuccessListener(v -> {
                        showToast("Expense Submitted. ID: " + expense.getId());
                        if (mContext instanceof android.app.Activity) {
                            ((android.app.Activity) mContext).finish();
                        }
                    })
                    .addOnFailureListener(e -> showToast("Submission failed: " + e.getMessage()));
            });
    }

    @JavascriptInterface
    public void updateExpenseStatus(String expenseId, String status) {
        FirestoreRepository.getInstance().updateExpenseStatus(expenseId, status)
            .addOnSuccessListener(aVoid -> {
                showToast("Expense " + status);
                safeEval("if(window.loadFinancials) window.loadFinancials();");
            })
            .addOnFailureListener(e -> showToast("Status update failed: " + e.getMessage()));
    }

    @JavascriptInterface
    public void deleteExpense(String expenseId) {
        FirestoreRepository.getInstance().deleteExpense(expenseId)
            .addOnSuccessListener(aVoid -> {
                showToast("Expense Record Deleted");
                safeEval("if(window.loadFinancials) window.loadFinancials();");
            })
            .addOnFailureListener(e -> showToast("Deletion failed: " + e.getMessage()));
    }

    @JavascriptInterface
    public void clearFinancials() {
        FirestoreRepository.getInstance().clearFinancials()
            .addOnSuccessListener(aVoid -> {
                showToast("Financial Ledger Cleared.");
                safeEval("if(window.loadFinancials) window.loadFinancials();");
            });
    }

    @JavascriptInterface
    public void loadStaff() {
        FirestoreRepository.getInstance().getStaffJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String json = task.getResult();
                safeEval("if(window.renderStaff) { var d = " + safeJson(json) + "; window.renderStaff(typeof d === 'string' ? JSON.parse(d) : d); }");
            }
        });
    }

    @JavascriptInterface
    public void loadPayrollRecords() {
        FirestoreRepository.getInstance().getStaffJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String json = task.getResult();
                // We map staff to a payroll format: {name, amount, role, status}
                // For now we use mock amounts as we don't have a Salary field yet
                safeEval("if(window.renderPayroll) { " +
                        "var d = " + safeJson(json) + "; " +
                        "var staff = (typeof d === 'string') ? JSON.parse(d) : d; " +
                        "var records = staff.map(s => ({ name: s.name, amount: 25000, role: s.rank || 'Coach', status: 'PAID' })); " +
                        "window.renderPayroll(records); }");
            }
        });
    }

    @JavascriptInterface
    public void loadTasks() {
        FirestoreRepository.getInstance().getTasksJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String json = task.getResult();
                safeEval("if(window.renderTasks) { var d = " + safeJson(json) + "; window.renderTasks(typeof d === 'string' ? JSON.parse(d) : d); }");
            }
        });
    }

    @JavascriptInterface
    public void loadRosterForPerformance() {
        FirestoreRepository.getInstance().getPlayersJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String json = task.getResult();
                safeEval("if(window.renderRoster) { var d = " + safeJson(json) + "; window.renderRoster(typeof d === 'string' ? JSON.parse(d) : d); }");
            }
        });
    }

    @JavascriptInterface
    public void loadPlayerAnalytics(String studentId) {
        String uid = ("me".equals(studentId)) ? FirebaseAuth.getInstance().getCurrentUser().getUid() : studentId;
        
        FirestoreRepository.getInstance().getDb().collection("Users").document(uid).get()
            .addOnCompleteListener(userTask -> {
                String userJson = "{}";
                if (userTask.isSuccessful() && userTask.getResult() != null && userTask.getResult().exists()) {
                    User user = userTask.getResult().toObject(User.class);
                    if (user != null) {
                        user.setId(userTask.getResult().getId());
                        userJson = new Gson().toJson(user);
                    }
                }
                
                final String finalUserJson = userJson;
                FirestoreRepository.getInstance().getTasksJson().addOnCompleteListener(tasksTask -> {
                    String tasksJson = "[]";
                    if (tasksTask.isSuccessful()) {
                        tasksJson = tasksTask.getResult();
                    }
                    
                    // We can reuse the tasks JSON for both history and tasks, since JS handles filtering
                    String js = "if(window.renderAnalytics) { " +
                                "var u = " + safeJson(finalUserJson) + "; " +
                                "var t = " + safeJson(tasksJson) + "; " +
                                "var pU = (typeof u === 'string') ? JSON.parse(u) : u; " +
                                "var pT = (typeof t === 'string') ? JSON.parse(t) : t; " +
                                "var h = pT.filter(x => (x.assignedTo === '" + uid + "' || x.assignedTo === 'All') && x.status === 'Completed' && x.coachFeedback).map(x => ({score: x.score, feedback: x.coachFeedback, timestamp: x.timestamp})); " +
                                "window.renderAnalytics(pU, h, pT); }";
                    safeEval(js);
                });
            });
    }

    @JavascriptInterface
    public void submitPerformance(String playerId, String rating, String feedback) {
        FirestoreRepository.getInstance().submitPerformance(playerId, rating, feedback)
            .addOnSuccessListener(aVoid -> {
                showToast("Performance Published!");
                if (mContext instanceof android.app.Activity) ((android.app.Activity) mContext).finish();
            })
            .addOnFailureListener(e -> showToast("Failed: " + e.getMessage()));
    }

    @JavascriptInterface
    public void loadAllPlayers() {
        FirestoreRepository.getInstance().getPlayersJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String json = task.getResult();
                safeEval("if(window.renderRoster) { var d = " + safeJson(json) + "; window.renderRoster(typeof d === 'string' ? JSON.parse(d) : d); }");
            }
        });
    }

    @JavascriptInterface
    public void openPlayerAnalytics(String id) {
        openPlayerAnalytics(id, "overview");
    }

    @JavascriptInterface
    public void openPlayerAnalytics(String id, String tab) {
        Intent intent = new Intent(mContext, StudentAnalyticsActivity.class);
        intent.putExtra("STUDENT_ID", id);
        intent.putExtra("INITIAL_TAB", tab);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void loadFaq() {
        FirestoreRepository.getInstance().getFaqJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String json = task.getResult();
                safeEval("if(window.renderFaq) { var d = " + safeJson(json) + "; window.renderFaq(typeof d === 'string' ? JSON.parse(d) : d); }");
            }
        });
    }

    @JavascriptInterface
    public void loadFeedbackList() {
        // We fetch anonymous feedback from "Feedback" collection
        FirestoreRepository.getInstance().getDb().collection("Feedback").orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING).get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : task.getResult()) {
                        list.add(doc.getData());
                    }
                    String json = new Gson().toJson(list);
                    safeEval("if(window.renderFeedbackList) { var d = " + safeJson(json) + "; window.renderFeedbackList(typeof d === 'string' ? JSON.parse(d) : d); }");
                }
            });
    }

    @JavascriptInterface
    public void submitFeedbackContent(String content) {
        java.util.Map<String, Object> f = new java.util.HashMap<>();
        f.put("content", content);
        f.put("timestamp", System.currentTimeMillis());
        f.put("subject", "General Concern");
        
        FirestoreRepository.getInstance().getDb().collection("Feedback").add(f)
            .addOnSuccessListener(ref -> {
                showToast("Feedback submitted successfully.");
                safeEval("if(window.Android) window.Android.finishActivity();");
            })
            .addOnFailureListener(e -> showToast("Error: " + e.getMessage()));
    }

    @JavascriptInterface
    public void loadChatGroups() {
        FirestoreRepository.getInstance().getChatGroupsJson().addOnCompleteListener(groupTask -> {
            String groupsJson = groupTask.isSuccessful() ? groupTask.getResult() : "[]";
            FirestoreRepository.getInstance().getUserDataJson().addOnCompleteListener(userTask -> {
                String userJson = userTask.isSuccessful() ? userTask.getResult() : "{}";
                safeEval("if(window.renderChats) { var g = " + safeJson(groupsJson) + "; var u = " + safeJson(userJson) + "; window.renderChats(typeof g === 'string' ? JSON.parse(g) : g, typeof u === 'string' ? JSON.parse(u) : u); }");
            });
        });
    }

    @JavascriptInterface
    public void loadChatMessages(String groupId) {
        FirestoreRepository.getInstance().getUserDataJson().addOnCompleteListener(userTask -> {
            String userJson = userTask.isSuccessful() ? userTask.getResult() : "{}";
            FirestoreRepository.getInstance().getDb()
                .collection("ChatGroups").document(groupId).collection("Messages")
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        safeEval("if(window.renderMessages) window.renderMessages([], " + safeJson(userJson) + ");");
                        return;
                    }
                    java.util.List<com.example.athleo.models.ChatMessage> msgs = value.toObjects(com.example.athleo.models.ChatMessage.class);
                    String messagesJson = new Gson().toJson(msgs);
                    safeEval("if(window.renderMessages) { var m = " + safeJson(messagesJson) + "; var u = " + safeJson(userJson) + "; window.renderMessages(typeof m === 'string' ? JSON.parse(m) : m, typeof u === 'string' ? JSON.parse(u) : u); }");
                });
        });
    }

    @JavascriptInterface
    public void sendChatMessage(String groupId, String text) {
        FirestoreRepository.getInstance().getUserDataJson().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                User user = new Gson().fromJson(userTask.getResult(), User.class);
                if (user != null) {
                    FirestoreRepository.getInstance().sendChatMessage(groupId, user.getId(), user.getName(), user.getRole(), text)
                        .addOnFailureListener(e -> showToast("Failed to send: " + e.getMessage()));
                }
            }
        });
    }

    @JavascriptInterface
    public void loadAllUsers() {
        FirestoreRepository.getInstance().getAllUsersJson().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String usersJson = task.getResult();
                safeEval("if(window.renderUsers) { var d = " + safeJson(usersJson) + "; window.renderUsers(typeof d === 'string' ? JSON.parse(d) : d); }");
            }
        });
    }

    @JavascriptInterface
    public void createChatGroup(String name, String description, String memberIdsJson) {
        FirestoreRepository.getInstance().getUserDataJson().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                User user = new Gson().fromJson(userTask.getResult(), User.class);
                if (user != null) {
                    // Role Validation
                    String role = user.getRole();
                    if (role != null && role.equalsIgnoreCase("Student")) {
                        showToast("Access Denied: Students cannot create groups.");
                        return;
                    }

                    try {
                        java.util.List<String> memberIds = new Gson().fromJson(memberIdsJson, new com.google.gson.reflect.TypeToken<java.util.List<String>>(){}.getType());
                        FirestoreRepository.getInstance().createChatGroup(name, description, memberIds, user.getId())
                            .addOnSuccessListener(aVoid -> {
                                showToast("Group created!");
                                safeEval("if(window.loadChatGroups) window.loadChatGroups();");
                            })
                            .addOnFailureListener(e -> showToast("Error: " + e.getMessage()));
                    } catch (Exception e) { showToast("Invalid member data."); }
                }
            }
        });
    }

    @JavascriptInterface
    public void removeMember(String groupId, String userId) {
        FirestoreRepository.getInstance().getUserDataJson().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                User user = new Gson().fromJson(userTask.getResult(), User.class);
                if (user != null) {
                    // Role Validation
                    String role = user.getRole();
                    if (role != null && role.equalsIgnoreCase("Student")) {
                        showToast("Access Denied.");
                        return;
                    }

                    FirestoreRepository.getInstance().removeMemberFromChatGroup(groupId, userId)
                        .addOnSuccessListener(aVoid -> {
                            showToast("Member removed.");
                            safeEval("if(window.onGroupUpdated) window.onGroupUpdated();");
                        })
                        .addOnFailureListener(e -> showToast("Failed: " + e.getMessage()));
                }
            }
        });
    }

    @JavascriptInterface
    public void deleteChatGroup(String groupId) {
        FirestoreRepository.getInstance().getUserDataJson().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                User user = new Gson().fromJson(userTask.getResult(), User.class);
                if (user != null) {
                    // Role Validation
                    String role = user.getRole();
                    if (role != null && role.equalsIgnoreCase("Student")) {
                        showToast("Access Denied: Students cannot delete groups.");
                        return;
                    }

                    FirestoreRepository.getInstance().deleteChatGroup(groupId)
                        .addOnSuccessListener(aVoid -> {
                            showToast("Group deleted successfully.");
                            safeEval("if(window.loadChatGroups) window.loadChatGroups();");
                        })
                        .addOnFailureListener(e -> showToast("Deletion failed: " + e.getMessage()));
                }
            }
        });
    }

    @JavascriptInterface
    public void getChatGroupInfo(String groupId) {
        FirestoreRepository.getInstance().getChatGroupJson(groupId).addOnCompleteListener(task -> {
            String groupJson = task.isSuccessful() ? task.getResult() : "{}";
            FirestoreRepository.getInstance().getAllUsersJson().addOnCompleteListener(usersTask -> {
                String usersJson = usersTask.isSuccessful() ? usersTask.getResult() : "[]";
                safeEval("if(window.renderGroupInfo) { var g = " + safeJson(groupJson) + "; var u = " + safeJson(usersJson) + "; window.renderGroupInfo(typeof g === 'string' ? JSON.parse(g) : g, typeof u === 'string' ? JSON.parse(u) : u); }");
            });
        });
    }

    @JavascriptInterface
    public void updateChatGroupMembers(String groupId, String memberIdsJson, String adminIdsJson) {
        try {
            java.util.List<String> memberIds = new Gson().fromJson(memberIdsJson, new com.google.gson.reflect.TypeToken<java.util.List<String>>(){}.getType());
            java.util.List<String> adminIds = new Gson().fromJson(adminIdsJson, new com.google.gson.reflect.TypeToken<java.util.List<String>>(){}.getType());
            FirestoreRepository.getInstance().updateChatGroupMembers(groupId, memberIds, adminIds)
                .addOnSuccessListener(aVoid -> {
                    showToast("Group updated!");
                    safeEval("if(window.onGroupUpdated) window.onGroupUpdated()");
                })
                .addOnFailureListener(e -> showToast("Error: " + e.getMessage()));
        } catch (Exception e) { showToast("Invalid update data."); }
    }

    // ACTION METHODS
    @JavascriptInterface
    public void openChatRoom(String id, String name) {
        Intent intent = new Intent(mContext, ChatRoomActivity.class);
        intent.putExtra("GROUP_ID", id);
        intent.putExtra("GROUP_NAME", name);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void saveProfile(String name, String profileImageUrl) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();
            FirestoreRepository.getInstance().updateUserProfile(uid, name, profileImageUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) safeEval("if(window.onProfileSaved) window.onProfileSaved(true)");
                    else safeEval("if(window.onProfileSaved) window.onProfileSaved(false, '" + task.getException().getMessage().replace("'", "\\'") + "')");
                });
        }
    }

    @JavascriptInterface
    public void finishActivity() { if (mContext instanceof android.app.Activity) ((android.app.Activity) mContext).finish(); }

    @JavascriptInterface
    public void createTaskForPlayer(String title, String description, String category, String studentId, String deadline) {
        FirestoreRepository.getInstance().createTaskForPlayer(title, description, category, studentId, deadline)
            .addOnSuccessListener(aVoid -> {
                showToast("Task Assigned!");
                safeEval("if(window.onTaskCreated) window.onTaskCreated()");
                if (mContext instanceof android.app.Activity) {
                    ((android.app.Activity) mContext).finish();
                }
            })
            .addOnFailureListener(e -> showToast("Error: " + e.getMessage()));
    }

    @JavascriptInterface
    public void openFilePicker(String taskId) {
        if (mContext instanceof TaskManagementActivity) {
            ((TaskManagementActivity) mContext).setPendingTaskId(taskId);
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            ((TaskManagementActivity) mContext).startActivityForResult(intent, 1);
        }
    }

    @JavascriptInterface
    public void completeTask(String taskId, String proofUrl) {
         FirestoreRepository.getInstance().completeTask(taskId, proofUrl)
            .addOnSuccessListener(aVoid -> {
                showToast("Task Completed!");
                safeEval("if(window.loadTasks) window.loadTasks()");
            })
            .addOnFailureListener(e -> showToast("Completion failed: " + e.getMessage()));
    }

    @JavascriptInterface
    public void evaluateTask(String taskId, String status, int score, String feedback) {
        FirestoreRepository.getInstance().evaluateTask(taskId, status, score, feedback)
            .addOnSuccessListener(aVoid -> {
                showToast("Task Evaluated!");
                safeEval("if(window.loadTasks) window.loadTasks()");
            })
            .addOnFailureListener(e -> showToast("Evaluation failed: " + e.getMessage()));
    }

    @JavascriptInterface
    public void deleteTask(String taskId) {
        FirestoreRepository.getInstance().deleteTask(taskId)
            .addOnSuccessListener(aVoid -> {
                showToast("Task Deleted!");
                safeEval("if(window.loadTasks) window.loadTasks()");
            })
            .addOnFailureListener(e -> showToast("Deletion failed: " + e.getMessage()));
    }

    // SYSTEM METHODS
    @JavascriptInterface
    public void seedData() {
        FirestoreRepository.getInstance().seedMockData().addOnCompleteListener(task -> {
            if (task.isSuccessful()) showToast("Backend Seeded!");
            else showToast("Seed Failed: " + (task.getException() != null ? task.getException().getMessage() : "Error"));
        });
    }

    @JavascriptInterface
    public void seedTestAccounts() {
        showToast("Starting Global Init...");
        FirebaseAuth.getInstance().signOut();
        // Redirect logic handled via Activity if needed, but here we just process.
        createDirector();
    }

    private void createDirector() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword("director@athleo.com", "Director@123")
            .addOnCompleteListener(t -> {
                if (t.isSuccessful()) {
                    setupRole(t.getResult().getUser().getUid(), "Technical Director", "director@athleo.com", () -> createTrainer());
                } else {
                    auth.signInWithEmailAndPassword("director@athleo.com", "Director@123")
                        .addOnCompleteListener(signTask -> {
                            if (signTask.isSuccessful()) {
                                setupRole(signTask.getResult().getUser().getUid(), "Technical Director", "director@athleo.com", () -> createTrainer());
                            } else {
                                showToast("Director setup failed: " + signTask.getException().getMessage());
                                createTrainer();
                            }
                        });
                }
            });
    }

    private void createTrainer() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword("trainer@athleo.com", "Trainer@123")
            .addOnCompleteListener(t -> {
                if (t.isSuccessful()) {
                    setupRole(t.getResult().getUser().getUid(), "Trainer", "trainer@athleo.com", () -> createCoach());
                } else {
                    auth.signInWithEmailAndPassword("trainer@athleo.com", "Trainer@123")
                        .addOnCompleteListener(signTask -> {
                            if (signTask.isSuccessful()) {
                                setupRole(signTask.getResult().getUser().getUid(), "Trainer", "trainer@athleo.com", () -> createCoach());
                            } else {
                                showToast("Trainer setup failed: " + signTask.getException().getMessage());
                                createCoach();
                            }
                        });
                }
            });
    }

    private void createCoach() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword("coach@athleo.com", "Coach@123")
            .addOnCompleteListener(t -> {
                if (t.isSuccessful()) {
                    setupRole(t.getResult().getUser().getUid(), "Head Coach", "coach@athleo.com", () -> seedDataSequence());
                } else {
                    auth.signInWithEmailAndPassword("coach@athleo.com", "Coach@123")
                        .addOnCompleteListener(signTask -> {
                            if (signTask.isSuccessful()) {
                                setupRole(signTask.getResult().getUser().getUid(), "Head Coach", "coach@athleo.com", () -> seedDataSequence());
                            } else {
                                showToast("Coach setup failed: " + signTask.getException().getMessage());
                                auth.signOut();
                            }
                        });
                }
            });
    }

    private void seedDataSequence() {
        FirestoreRepository.getInstance().clearFinancials().continueWithTask(t -> 
            FirestoreRepository.getInstance().seedMockData()
        ).addOnCompleteListener(seedTask -> {
            if (seedTask.isSuccessful()) {
                showToast("Global Init Complete!");
            } else {
                showToast("Global Seed partially failed: " + seedTask.getException().getMessage());
            }
            FirebaseAuth.getInstance().signOut();
            showToast("Init Complete. Log in as Director/Coach.");
        });
    }

    private void setupRole(String uid, String role, String email, Runnable next) {
        User u = new User(uid, role, email, role);
        FirestoreRepository.getInstance().getDb().collection("Users").document(uid).set(u)
            .addOnCompleteListener(task -> { if(next != null) next.run(); });
    }

    // UTILITIES
    @JavascriptInterface
    public void openUri(String uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mContext.startActivity(Intent.createChooser(intent, "Open File"));
        } catch (Exception e) { showToast("No viewer found."); }
    }
}
