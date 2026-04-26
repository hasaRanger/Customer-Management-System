import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { customerApi } from "../api/api";

function Row({ label, value }) {
  return (
    <div style={{ display: "flex", padding: "8px 0", borderBottom: "1px solid #f1f5f9", alignItems: "center" }}>
      <span style={{ width: 140, fontSize: 13, fontWeight: 600, color: "#64748b" }}>{label}</span>
      <span style={{ fontSize: 14 }}>{value}</span>
    </div>
  );
}

export default function CustomerDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [customer, setCustomer] = useState(null);

  useEffect(() => {
    customerApi.getById(id).then(r => setCustomer(r.data));
  }, [id]);

  if (!customer) return <p style={{ padding: 32, textAlign: "center", color: "#64748b" }}>Loading...</p>;

  return (
    <div style={{ maxWidth: 680 }}>
      <div style={{ display: "flex", alignItems: "center", gap: 12, marginBottom: 24 }}>
        <button className="btn btn-secondary" onClick={() => navigate(-1)}>Back</button>
        <h1 style={{ fontSize: 20, fontWeight: 700 }}>Customer Detail</h1>
        <button className="btn btn-primary" style={{ marginLeft: "auto" }} onClick={() => navigate("/customers/" + id + "/edit")}>Edit</button>
      </div>

      <div className="card" style={{ marginBottom: 16 }}>
        <h3 style={{ fontSize: 15, fontWeight: 600, marginBottom: 16, color: "#334155" }}>Basic Information</h3>
        <Row label="Full Name" value={customer.name} />
        <Row label="Date of Birth" value={customer.dateOfBirth} />
        <Row label="NIC Number" value={<span className="badge badge-blue">{customer.nicNumber}</span>} />
      </div>

      <div className="card" style={{ marginBottom: 16 }}>
        <h3 style={{ fontSize: 15, fontWeight: 600, marginBottom: 12, color: "#334155" }}>Phone Numbers</h3>
        {customer.phoneNumbers.length ? (
          <div style={{ display: "flex", flexWrap: "wrap", gap: 8 }}>
            {customer.phoneNumbers.map((p, i) => (
              <span key={i} className="badge badge-green" style={{ fontSize: 13 }}>{p}</span>
            ))}
          </div>
        ) : <p style={{ color: "#94a3b8", fontSize: 14 }}>No phone numbers added</p>}
      </div>

      <div className="card" style={{ marginBottom: 16 }}>
        <h3 style={{ fontSize: 15, fontWeight: 600, marginBottom: 12, color: "#334155" }}>Addresses</h3>
        {customer.addresses.length ? customer.addresses.map((a, i) => (
          <div key={i} style={{ padding: "10px 14px", background: "#f8fafc", borderRadius: 6, marginBottom: 8 }}>
            <p style={{ fontWeight: 500 }}>{a.addressLine1}</p>
            {a.addressLine2 && <p style={{ color: "#64748b", fontSize: 13 }}>{a.addressLine2}</p>}
            <p style={{ color: "#64748b", fontSize: 13 }}>{[a.cityName, a.countryName].filter(Boolean).join(", ")}</p>
          </div>
        )) : <p style={{ color: "#94a3b8", fontSize: 14 }}>No addresses added</p>}
      </div>

      <div className="card">
        <h3 style={{ fontSize: 15, fontWeight: 600, marginBottom: 12, color: "#334155" }}>Family Members</h3>
        {customer.familyMembers.length ? (
          <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
            {customer.familyMembers.map(m => (
              <div key={m.id} style={{ display: "flex", alignItems: "center", justifyContent: "space-between", padding: "8px 12px", background: "#f8fafc", borderRadius: 6 }}>
                <span>{m.name} <span className="badge badge-blue" style={{ marginLeft: 6 }}>{m.nicNumber}</span></span>
                <button className="btn btn-secondary" style={{ padding: "4px 12px", fontSize: 13 }} onClick={() => navigate("/customers/" + m.id)}>View</button>
              </div>
            ))}
          </div>
        ) : <p style={{ color: "#94a3b8", fontSize: 14 }}>No family members linked</p>}
      </div>
    </div>
  );
}
