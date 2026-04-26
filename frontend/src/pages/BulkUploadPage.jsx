import React, { useState, useRef } from "react";
import { bulkUploadApi } from "../api/api";

export default function BulkUploadPage() {
  const [file, setFile] = useState(null);
  const [job, setJob] = useState(null);
  const [error, setError] = useState("");
  const [uploading, setUploading] = useState(false);
  const pollRef = useRef(null);

  const handleUpload = async () => {
    if (!file) return;
    setError(""); setJob(null); setUploading(true);
    const formData = new FormData();
    formData.append("file", file);
    try {
      const res = await bulkUploadApi.upload(formData);
      setJob(res.data);
      pollRef.current = setInterval(async () => {
        const s = await bulkUploadApi.getStatus(res.data.jobId);
        setJob(s.data);
        if (["DONE", "FAILED"].includes(s.data.status)) {
          clearInterval(pollRef.current);
          setUploading(false);
        }
      }, 2000);
    } catch (e) {
      setError(e.response?.data?.error || "Upload failed");
      setUploading(false);
    }
  };

  const progress = job?.totalRows ? Math.min(100, Math.round((job.processedRows / job.totalRows) * 100)) : 0;
  const statusColor = { DONE: "#16a34a", FAILED: "#dc2626", PROCESSING: "#1d4ed8", PENDING: "#a16207" };

  return (
    <div style={{ maxWidth: 580 }}>
      <h1 style={{ fontSize: 20, fontWeight: 700, marginBottom: 6 }}>Bulk Customer Upload</h1>
      <p style={{ color: "#64748b", marginBottom: 24 }}>Upload an Excel (.xlsx) file to create or update many customers at once.</p>

      <div className="card" style={{ marginBottom: 20, background: "#eff6ff", border: "1px solid #bfdbfe" }}>
        <p style={{ fontWeight: 600, marginBottom: 8, color: "#1d4ed8" }}>Required Excel Format</p>
        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 13 }}>
          <thead>
            <tr style={{ background: "#dbeafe" }}>
              {["Column A", "Column B", "Column C"].map(h => (
                <th key={h} style={{ padding: "6px 10px", textAlign: "left" }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            <tr>
              <td style={{ padding: "6px 10px" }}>Name</td>
              <td style={{ padding: "6px 10px" }}>Date of Birth (yyyy-MM-dd)</td>
              <td style={{ padding: "6px 10px" }}>NIC Number</td>
            </tr>
            <tr style={{ background: "#eff6ff" }}>
              <td style={{ padding: "6px 10px", color: "#64748b" }}>John Silva</td>
              <td style={{ padding: "6px 10px", color: "#64748b" }}>1990-05-15</td>
              <td style={{ padding: "6px 10px", color: "#64748b" }}>199005150123</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div className="card" style={{ marginBottom: 16 }}>
        <div style={{ border: "2px dashed #cbd5e1", borderRadius: 8, padding: 24, textAlign: "center", marginBottom: 16 }}>
          <input type="file" accept=".xlsx" onChange={e => { setFile(e.target.files[0]); setJob(null); }} style={{ display: "block", margin: "0 auto" }} />
          {file && <p style={{ marginTop: 10, fontSize: 13, color: "#1d4ed8", fontWeight: 500 }}>{file.name} ({(file.size / 1024 / 1024).toFixed(2)} MB)</p>}
        </div>
        <button className="btn btn-primary" onClick={handleUpload} disabled={!file || uploading} style={{ width: "100%", padding: 11, fontSize: 15 }}>
          {uploading ? "Processing..." : "Upload File"}
        </button>
      </div>

      {error && (
        <div style={{ background: "#fee2e2", border: "1px solid #fca5a5", borderRadius: 8, padding: "12px 16px", color: "#b91c1c", marginBottom: 16 }}>{error}</div>
      )}

      {job && (
        <div className="card">
          <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 12 }}>
            <span style={{ fontWeight: 600 }}>Job #{job.jobId}</span>
            <span style={{ fontWeight: 700, color: statusColor[job.status] || "#64748b" }}>{job.status}</span>
          </div>
          {job.totalRows > 0 && (
            <>
              <div style={{ background: "#e2e8f0", borderRadius: 4, height: 14, marginBottom: 8, overflow: "hidden" }}>
                <div style={{ width: progress + "%", background: "#1e40af", height: "100%", borderRadius: 4, transition: "width 0.4s ease" }} />
              </div>
              <p style={{ fontSize: 13, color: "#64748b", marginBottom: 4 }}>{job.processedRows} / {job.totalRows} rows processed ({progress}%)</p>
              {job.failedRows > 0 && <p style={{ fontSize: 13, color: "#dc2626" }}>{job.failedRows} rows failed (invalid data)</p>}
            </>
          )}
          {job.status === "DONE" && <p style={{ color: "#16a34a", fontWeight: 600, marginTop: 8 }}>Upload complete!</p>}
          {job.errorMessage && <p style={{ color: "#dc2626", fontSize: 13, marginTop: 8 }}>Error: {job.errorMessage}</p>}
        </div>
      )}
    </div>
  );
}
