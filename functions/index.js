const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendAnnouncementNotification = functions.firestore
  .document("Announcements/{docId}")
  .onCreate(async (snap, context) => {
    const data = snap.data();
    
    if (!data) return null;

    const payload = {
      notification: {
        title: data.title || "New Announcement!",
        body: data.message || "You have a new announcement from the Academy.",
      },
      topic: "announcements"
    };

    try {
      const response = await admin.messaging().send(payload);
      console.log("Successfully sent message:", response);
      return response;
    } catch (error) {
      console.error("Error sending message:", error);
      return null;
    }
  });
