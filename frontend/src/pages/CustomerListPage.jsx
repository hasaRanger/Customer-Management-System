import React, { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { customerApi } from "../api/api";

export default function CustomerListPage() {
  const [data, setData] = useState({ content: [], totalPages: 0, totalElements: 0 });
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const fetchData = useCallback(() => {
    setLoading(true);
    customerApi.getAll({ search: search || undefined, page, size: 20 })
      .then(r => setData(r.data))
      .finally(() => setLoading(false));
  }, [search, page]);

  useEffect(() => { fetchData(); }, [fetchData]);

  const handleDelete = async (id, name) => {
    if (!window.confirm("Delete customer " + name + "?")) return;
    await customerApi.delete(id);
    fetchData();
  };

  return (
    <div>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 20 }}>
        <h1 style={{ fontSize: 22, fontWeight: 700 }}>
          Customers
          <span style={{ fontSize: 14, fontWeight: 400, color: "#64748b", marginLeft: 10 }}>({data.totalElements} total)</span>
        </h1>
        <button className="btn btn-primary" onClick={() => navigate("/customers/new")}>+ New Customer</button>
      </div>

      <div className="card" style={{ marginBottom: 16 }}>
        <input
          placeholder="Search by name or NIC..."
          value={search}
          onChange={e => { setSearch(e.target.value); setPage(0); }}
          style={{ width: "100%", padding: "10px 14px", border: "1px solid #cbd5e1", borderRadius: 6, fontSize: 14 }}
        />
      </div>

      <div className="card" style={{ padding: 0, overflow: "hidden" }}>
        {loading ? (
          <p style={{ padding: 24, textAlign: "center", color: "#64748b" }}>Loading...</p>
        ) : data.content.length === 0 ? (
          <p style={{ padding: 24, textAlign: "center", color: "#64748b" }}>
            No customers found.{" "}
            <button className="btn btn-primary" style={{ marginLeft: 8 }} onClick={() => navigate("/customers/new")}>Add one</button>
          </p>
        ) : (
          <table style={{ width: "100%", borderCollapse: "collapse" }}>
            <thead>
              <tr style={{ background: "#f1f5f9", borderBottom: "2px solid #e2e8f0" }}>
                {["Name", "Date of Birth", "NIC Number", "Phones", "Addresses", "Actions"].map(h => (
                  <th key={h} style={{ padding: "12px 16px", textAlign: "left", fontSize: 13, fontWeight: 600, color: "#475569" }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {data.content.map((c, i) => (
                <tr key={c.id} style={{ borderBottom: "1px solid #f1f5f9", background: i % 2 === 0 ? "#fff" : "#fafafa" }}>
                  <td style={{ padding: "12px 16px", fontWeight: 500 }}>{c.name}</td>
                  <td style={{ padding: "12px 16px", color: "#64748b" }}>{c.dateOfBirth}</td>
                  <td style={{ padding: "12px 16px" }}><span className="badge badge-blue">{c.nicNumber}</span></td>
                  <td style={{ padding: "12px 16px", color: "#64748b" }}>{c.phoneCount}</td>
                  <td style={{ padding: "12px 16px", color: "#64748b" }}>{c.addressCount}</td>
                  <td style={{ padding: "12px 16px", display: "flex", gap: 6 }}>
                    <button className="btn btn-secondary" style={{ padding: "5px 12px", fontSize: 13 }} onClick={() => navigate("/customers/" + c.id)}>View</button>
                    <button className="btn btn-primary" style={{ padding: "5px 12px", fontSize: 13 }} onClick={() => navigate("/customers/" + c.id + "/edit")}>Edit</button>
                    <button className="btn btn-danger" style={{ padding: "5px 12px", fontSize: 13 }} onClick={() => handleDelete(c.id, c.name)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {data.totalPages > 1 && (
        <div style={{ marginTop: 16, display: "flex", gap: 8, alignItems: "center", justifyContent: "center" }}>
          <button className="btn btn-secondary" disabled={page === 0} onClick={() => setPage(p => p - 1)}>Previous</button>
          <span style={{ fontSize: 14, color: "#64748b" }}>Page {page + 1} of {data.totalPages}</span>
          <button className="btn btn-secondary" disabled={page + 1 >= data.totalPages} onClick={() => setPage(p => p + 1)}>Next</button>
        </div>
      )}
    </div>
  );
}
