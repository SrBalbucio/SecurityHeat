import { initializeApp } from "https://www.gstatic.com/firebasejs/9.17.2/firebase-app.js";
import { getAnalytics } from "https://www.gstatic.com/firebasejs/9.17.2/firebase-analytics.js";
const firebaseConfig = {
  apiKey: "AIzaSyAOBcPUlsaXlVaWGBnKDYHPuATyV5h3NeQ",
  authDomain: "securityheat.firebaseapp.com",
  databaseURL: "https://securityheat-default-rtdb.firebaseio.com",
  projectId: "securityheat",
  storageBucket: "securityheat.appspot.com",
  messagingSenderId: "149470938930",
  appId: "1:149470938930:web:aece063c0d4c76432e392b",
  measurementId: "G-X9E01JWGF7"
};

var app = initializeApp(firebaseConfig);
var analytics = getAnalytics(app);

export { app, analytics };

