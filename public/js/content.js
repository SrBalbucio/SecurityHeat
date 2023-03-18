import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";

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

const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
