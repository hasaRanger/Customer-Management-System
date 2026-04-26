import React from "react";
import { BrowserRouter, Routes, Route, Link, useLocation } from "react-router-dom";
import { MasterDataProvider } from "./context/MasterDataContext";
import CustomerListPage from "./pages/CustomerListPage";
import CustomerFormPage from "./pages/CustomerFormPage";
import CustomerDetailPage from "./pages/CustomerDetailPage";
import BulkUploadPage from "./pages/BulkUploadPage";
import "./App.css";

function NavBar() {
  const location = useLocation();
  const isActive = (path) => location.pathname === path;
  const linkStyle = (path) => ({
    color: isActive(path) ? "#93c5fd" : "#fff",
    textDecoration: "none",
    fontWeight: isActive(path) ? 700 : 400,
    padding: "6px 14px",
    borderRadius: 6,
    background: isActive(path) ? "rgba(255,255,255,0.12)" : "transparent",
  });
  return (
    <nav style={{ padding: "14px 32px", background: "#1e40af", display: "flex", alignItems: "center", gap: 8, boxShadow: "0 2px 8px rgba(0,0,0,0.2)" }}>
      <span style={{ color: "#fff", fontWeight: 700, fontSize: 18, marginRight: 24 }}>Customer Management</span>
      <Link to="/" style={linkStyle("/")}>Customers</Link>
      <Link to="/customers/new" style={linkStyle("/customers/new")}>+ New Customer</Link>
      <Link to="/bulk-upload" style={linkStyle("/bulk-upload")}>Bulk Upload</Link>
    </nav>
  );
}

function AppRoutes() {
  return (
    <>
      <NavBar />
      <div style={{ padding: "28px 32px", maxWidth: 1200, margin: "0 auto" }}>
        <Routes>
          <Route path="/" element={<CustomerListPage />} />
          <Route path="/customers/new" element={<CustomerFormPage />} />
          <Route path="/customers/:id/edit" element={<CustomerFormPage />} />
          <Route path="/customers/:id" element={<CustomerDetailPage />} />
          <Route path="/bulk-upload" element={<BulkUploadPage />} />
        </Routes>
      </div>
    </>
  );
}

export default function App() {
  return (
    <MasterDataProvider>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </MasterDataProvider>
  );
}