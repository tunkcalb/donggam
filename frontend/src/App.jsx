import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./index.css";
import MainPage from "./pages/MainPage";
import LoginPage from "./pages/LoginPage";
import MailboxPage from "./pages/MailboxPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/mailbox/:userId" element={<MailboxPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
