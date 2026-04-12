# Project Report: Athleo - Sports Academy Management SaaS

## 1. Abstract
The Athleo Application introduces a modern, centralized platform for sports academies. This report details the architecture, module breakdown, technologies, and implementation strategies behind Athleo. The application aims to replace fragmented physical workflows (attendance, task grading, expense tracking, and communication) with a unified, role-based mobile ecosystem.

---

## 2. System Architecture
The application employs a **Hybrid-Native Architecture**:
1.  **Native Lifecycle Shell**: Built in Java for Android, handling process lifecycles, permissions, Firebase SDK initialization, and hardware intents (e.g., File Choosers for image uploads).
2.  **WebView Rendering Engine**: The UI is entirely decoupled from Android XML layouts (excluding the container). Instead, it relies on dynamically loaded, locally hosted HTML/JS files styled with Tailwind CSS. This allows for rapid, pixel-perfect web design (Glassmorphism, dark mode) without the constraints of native Android styling.
3.  **Javascript Bridge**: A custom `WebAppInterface.java` class acts as the central nervous system. It intercepts Javascript calls (e.g., `Android.openChatRoom()`) to launch native Activities, and pushes data down to the UI (e.g., `window.renderProfile(json)`) by converting Firebase Firestore snapshots into safe JSON strings using `Gson`.

---

## 3. Database Schema (Firebase Firestore)
Athleo operates on a NoSQL document-based structure optimized for real-time reads:

*   **`Users` Collection**: Stores unified accounts. Fields include `name`, `role` (Technical Director, Head Coach, Trainer, Student), `attendancePercentage`, `performanceScore`, and `profileImageUrl`.
*   **`Announcements` Collection**: Stores global broadcast notices. Fields: `title`, `content`, `type`, `timestamp`.
*   **`ChatGroups` Collection**: Manages team channels.
    *   Sub-collection: `Messages` (Sender ID, text, timestamp).
    *   Fields: `name`, `memberIds` (Array), `adminIds` (Array), `type` (default_all, default_staff, custom).
*   **`Tasks` Collection**: Stores training drills. Fields: `title`, `description`, `dueDate`, `assignedTo` (Array).
    *   Sub-collection: `Submissions` (Student ID, video URL, grade).
*   **`Expenses` Collection**: Tracks financial vetting. Fields: `amount`, `category`, `status` (PENDING, APPROVED, VOID).

---

## 4. Module Implementation Details

### 4.1 Hierarchical Routing & Dashboards
Upon Firebase Authentication success, the user’s role is fetched. The `MainActivity` dynamically swaps Fragment containers based on this role, injecting the associated HTML UI file (e.g., `student_dashboard_dark.html`). The Android `onResume` lifecycle hook is tied directly to the WebView, ensuring that returning from an external activity forces the Javascript to seamlessly re-fetch and re-render dynamic widgets (like `Total Students` or the `Profile Avatar`) without requiring a manual refresh.

### 4.2 Attendance & Performance System
The Trainer dashboard features an Attendance Tracker loaded with custom Javascript logic. It fetches all players from the database and renders a list with discrete Present/Absent toggle parameters. Upon submission, the payload is serialized and sent to native Android methods that execute batch writes to Firebase, recalculating global attendance percentages for analytical reporting on the Head Coach and Student dashboards.

### 4.3 Real-Time Community Chat
To support dynamic team communication, the app utilizes Firebase Snapshot Listeners. When the user opens the `ChatRoomActivity`, a persistent listener attaches to the specific `ChatGroups/{id}/Messages` sub-collection. When a new message is detected, the native Java code utilizes `Gson` to safely wrap the JSON payload (preventing syntax crashes from single quotes or newlines) and executes `#window.renderMessages` in the WebView, visually updating the chat feed instantaneously.

### 4.4 Profile Picture & Media Engine
Profile customization bypasses standard web file inputs. Tapping the edit profile avatar invokes a Javascript bridge calling `Android.chooseImage()`, which intercepts the local `WebChromeClient#onShowFileChooser` implementation. This surfaces the native Android file explorer. The selected image URI is passed to the HTML environment where a localized `Cropper.js` modal is launched. The user clips the image, and the resulting Base64 map is pushed back to Firebase Cloud Storage, updating the global user document.

---

## 5. UI/UX Design Philosophy
To ensure a premium, modern feel, Athleo completely standardizes its inputs globally. 
*   **Global Inputs**: A dedicated Node.js build-script was utilized across all 45+ UI templates to forcefully inject absolute CSS rules. Every textbox, password field, and dropdown implements a uniform surface background (`#171717`), a subtle border (`#262626`), and a distinct neon-green focus glow.
*   **Native Scrollbars**: All web-based scrolling mechanics were overridden. `webView.setVerticalScrollBarEnabled(false)` natively strips WebKit artifacts, creating a seamless app-like navigational experience that feels indistinguishable from a Jetpack Compose or Flutter application.

---

## 6. Challenges & Solutions
1.  **JSON Payload Injection Crashes**: Passing highly dynamic string data (like chat messages) from Java into Javascript via `evaluateJavascript` caused runtime crashes due to unescaped quotes. *Solution*: Implemented `Gson.toJson()` to safely encode all string variables prior to JavaScript injection.
2.  **WebView Form Styling**: Native Android WebViews often force clunky dropdown boxes (`<select>`). *Solution*: Injected an `appearance-none` CSS macro combined with a custom Base64-encoded SVG chevron into the global CSS logic, stripping the Android-native UI entirely.
3.  **Layout Shifts**: The Cropper.js matrix initially struggled with absolute positioning boundaries. *Solution*: Hard-locked the Cropper container to absolute block dimensions (`overflow: hidden`), stabilizing the responsive rendering logic.

---

## 7. Conclusion & Future Scope
The Athleo SaaS application successfully demonstrates the viability of a Hybrid-Native architecture for complex, multi-role organizational tools. By tightly coupling a Javascript-driven Tailwind CSS UI with robust Android/Firebase backend mechanics, the application achieves high performance, real-time data synchronization, and a premium aesthetic.

**Future Scope:**
*   Implementation of an external Node.js backend to facilitate global Firebase Cloud Messaging (FCM) Push Notifications.
*   Integration with a payment gateway (e.g., Stripe/Razorpay) for centralizing student fee collection.
*   Advanced tactical video analysis tools using a localized HTML5 Canvas drawing engine.
