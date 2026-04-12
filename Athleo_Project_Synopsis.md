# Project Synopsis: Athleo - Sports Academy Management SaaS

## 1. Introduction
**Athleo** is a comprehensive, centralized Sports Academy Management Application designed to bridge the operational gap between Technical Directors, Head Coaches, Trainers, and Students. Built as a native Android application powered by an embedded, highly responsive Web UI, Athleo provides a seamless, cross-functional platform for managing all aspects of a modern sports academy. From financial vetting and staff management to real-time attendance tracking and player performance analytics, Athleo digitizes the entire academy ecosystem.

## 2. Problem Statement
Traditional sports academies rely on fragmented communication channels (WhatsApp groups, SMS), paper-based attendance sheets, disjointed financial records, and manual task assignments. This leads to:
*   **Information Silos**: Coaches and students lack a unified schedule and communication hub.
*   **Inefficient Tracking**: Tracking daily attendance and long-term player performance metrics is tedious and prone to manual error.
*   **Financial Opacity**: Technical Directors lack real-time visibility into operational expenses and billing.
*   **Segmented Workflows**: Assigning physical/tactical tasks to students and reviewing their video proof requires juggling multiple third-party apps.

## 3. Proposed Solution
Athleo proposes a unified Software-as-a-Service (SaaS) architecture that consolidates scheduling, analytics, task management, and communication into a single, role-based application. Users are authenticated and routed to specialized dashboard interfaces tailored to their specific operational needs within the academy hierarchy.

## 4. Key Features & Modules

### 4.1 Hierarchical Role-Based Access
*   **Technical Director / Admin**: Complete oversight over academy operations. Features include a Financial Vetting System (approve/void expenses), staff management, and total student analytics.
*   **Head Coach**: Focuses on high-level team Strategy. Features include reviewing cumulative performance reports, managing subordinate trainers, assigning academy-wide tasks, and monitoring total attendance metrics.
*   **Trainer**: The ground-level operator. Features include marking daily squad attendance, submitting post-match performance ratings for individual students, and reviewing submitted task proofs.
*   **Student**: The end-user. Features include accessing personalized training schedules, viewing their own performance analytics, submitting video/image proof for assigned drills, and chatting with peers/coaches.

### 4.2 Core Systems
*   **App-Wide Community Chat:** A real-time, WhatsApp-style messaging interface supporting default academy-wide broadcasts, staff-only channels, and custom ad-hoc group creation with admin member management.
*   **Robust Attendance Tracking:** A custom UI allowing trainers to filter by squad and date, quickly toggling Present/Absent statuses that automatically sync to the cloud and update the student's global attendance percentage.
*   **Task Hub & Media Engine:** Coaches can assign physical or analytical tasks with specific deadlines. Students record themselves performing the drill and upload the video proof directly into the app for grading.
*   **Profile Management:** An integrated Cropper.js engine allowing users to upload, scale, and crop their profile avatars, dynamically updating references globally across the app.

## 5. Technology Stack
*   **Frontend UI Engine**: HTML5, Tailwind CSS, Javascript (Rendered via high-performance Android WebViews).
*   **Design Language**: Minimalist Dark Mode, Glassmorphism, Material Symbols.
*   **Backend Interface**: Android Java (Bridged to JavaScript natively via `@JavascriptInterface`).
*   **Database & Cloud Storage**: Google Firebase Firestore (NoSQL Document Database) and Firebase Cloud Storage (Images/Media).
*   **Authentication**: Firebase Authentication.
*   **Data Serialization**: Google Gson.
