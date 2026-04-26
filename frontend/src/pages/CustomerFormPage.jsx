import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useForm, useFieldArray } from "react-hook-form";
import DatePicker from "react-datepicker";
import Select from "react-select";
import "react-datepicker/dist/react-datepicker.css";
import { useMasterData } from "../context/MasterDataContext";
import { customerApi } from "../api/api";

export default function CustomerFormPage() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();
  const { countries, cities } = useMasterData();
  const [serverError, setServerError] = useState("");
  const [allCustomers, setAllCustomers] = useState([]);
  const [selectedFamily, setSelectedFamily] = useState([]);

  const { register, control, handleSubmit, setValue, watch, formState: { errors, isSubmitting } } = useForm({
    defaultValues: {
      name: "", nicNumber: "", dateOfBirth: null,
      phoneNumbers: [{ value: "" }],
      addresses: [],
    },
  });

  const { fields: phoneFields, append: addPhone, remove: removePhone } = useFieldArray({ control, name: "phoneNumbers" });
  const { fields: addrFields, append: addAddr, remove: removeAddr } = useFieldArray({ control, name: "addresses" });
  const dobValue = watch("dateOfBirth");

  useEffect(() => {
    customerApi.getAll({ size: 500 }).then(r =>
      setAllCustomers(r.data.content
        .filter(c => !id || String(c.id) !== id)
        .map(c => ({ value: c.id, label: c.name + " (" + c.nicNumber + ")" }))
      )
    );
  }, [id]);

  useEffect(() => {
    if (!isEdit) return;
    customerApi.getById(id).then(r => {
      const c = r.data;
      setValue("name", c.name);
      setValue("nicNumber", c.nicNumber);
      setValue("dateOfBirth", c.dateOfBirth ? new Date(c.dateOfBirth) : null);
      setValue("phoneNumbers", c.phoneNumbers.length ? c.phoneNumbers.map(p => ({ value: p })) : [{ value: "" }]);
      setValue("addresses", c.addresses.map(a => ({ addressLine1: a.addressLine1 || "", addressLine2: a.addressLine2 || "", cityId: null, countryId: null })));
      setSelectedFamily(c.familyMembers.map(m => ({ value: m.id, label: m.name + " (" + m.nicNumber + ")" })));
    });
  }, [id, isEdit, setValue]);

  const countryOptions = countries.map(c => ({ value: c.id, label: c.name }));

  const onSubmit = async (formData) => {
    setServerError("");
    if (!formData.dateOfBirth) {
      setServerError("Date of birth is required");
      return;
    }
    const payload = {
      name: formData.name,
      nicNumber: formData.nicNumber,
      dateOfBirth: formData.dateOfBirth.toISOString().split("T")[0],
      phoneNumbers: formData.phoneNumbers.map(p => p.value).filter(Boolean),
      addresses: formData.addresses.map(a => ({
        addressLine1: a.addressLine1,
        addressLine2: a.addressLine2,
        cityId: a.cityId || null,
        countryId: a.countryId || null,
      })),
      familyMemberIds: selectedFamily.map(f => f.value),
    };
    try {
      if (isEdit) {
        await customerApi.update(id, payload);
      } else {
        await customerApi.create(payload);
      }
      navigate("/");
    } catch (err) {
      setServerError(err.response?.data?.error || JSON.stringify(err.response?.data) || "An error occurred");
    }
  };

  return (
    <div style={{ maxWidth: 760 }}>
      <div style={{ display: "flex", alignItems: "center", gap: 12, marginBottom: 24 }}>
        <button className="btn btn-secondary" onClick={() => navigate(-1)}>Back</button>
        <h1 style={{ fontSize: 20, fontWeight: 700 }}>{isEdit ? "Edit Customer" : "New Customer"}</h1>
      </div>

      {serverError && (
        <div style={{ background: "#fee2e2", border: "1px solid #fca5a5", borderRadius: 8, padding: "12px 16px", marginBottom: 20, color: "#b91c1c" }}>
          {serverError}
        </div>
      )}

      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="card" style={{ marginBottom: 16 }}>
          <h3 style={{ fontSize: 15, fontWeight: 600, marginBottom: 16, color: "#334155" }}>Basic Information</h3>

          <div className="form-group">
            <label>Full Name *</label>
            <input {...register("name", { required: "Name is required" })} placeholder="Enter full name" />
            {errors.name && <p className="error-text">{errors.name.message}</p>}
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 16 }}>
            <div className="form-group">
              <label>NIC Number *</label>
              <input {...register("nicNumber", { required: "NIC is required" })} placeholder="e.g. 199005150123" />
              {errors.nicNumber && <p className="error-text">{errors.nicNumber.message}</p>}
            </div>
            <div className="form-group">
              <label>Date of Birth *</label>
              <DatePicker
                selected={dobValue}
                onChange={d => setValue("dateOfBirth", d)}
                dateFormat="yyyy-MM-dd"
                showYearDropdown
                scrollableYearDropdown
                yearDropdownItemNumber={80}
                placeholderText="Select date"
                maxDate={new Date()}
                customInput={<input style={{ width: "100%", padding: "9px 12px", border: "1px solid #cbd5e1", borderRadius: 6, fontSize: 14 }} />}
              />
            </div>
          </div>
        </div>

        <div className="card" style={{ marginBottom: 16 }}>
          <h3 style={{ fontSize: 15, fontWeight: 600, marginBottom: 16, color: "#334155" }}>Phone Numbers</h3>
          {phoneFields.map((f, i) => (
            <div key={f.id} style={{ display: "flex", gap: 8, marginBottom: 10 }}>
              <input {...register("phoneNumbers." + i + ".value")} placeholder="07XXXXXXXX"
                style={{ flex: 1, padding: "9px 12px", border: "1px solid #cbd5e1", borderRadius: 6, fontSize: 14 }} />
              {phoneFields.length > 1 && (
                <button type="button" className="btn btn-danger" style={{ padding: "6px 12px" }} onClick={() => removePhone(i)}>X</button>
              )}
            </div>
          ))}
          <button type="button" className="btn btn-secondary" onClick={() => addPhone({ value: "" })}>+ Add Phone</button>
        </div>

        <div className="card" style={{ marginBottom: 16 }}>
          <h3 style={{ fontSize: 15, fontWeight: 600, marginBottom: 16, color: "#334155" }}>Addresses</h3>
          {addrFields.map((f, i) => {
            const selectedCountryId = watch("addresses." + i + ".countryId");
            const cityOptions = cities
              .filter(c => !selectedCountryId || c.countryId === selectedCountryId)
              .map(c => ({ value: c.id, label: c.name }));
            return (
              <div key={f.id} style={{ border: "1px solid #e2e8f0", borderRadius: 8, padding: 16, marginBottom: 12 }}>
                <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 12 }}>
                  <span style={{ fontWeight: 600, fontSize: 13, color: "#64748b" }}>Address {i + 1}</span>
                  <button type="button" className="btn btn-danger" style={{ padding: "3px 10px", fontSize: 12 }} onClick={() => removeAddr(i)}>Remove</button>
                </div>
                <div className="form-group">
                  <label>Address Line 1</label>
                  <input {...register("addresses." + i + ".addressLine1")} placeholder="Street address" />
                </div>
                <div className="form-group">
                  <label>Address Line 2</label>
                  <input {...register("addresses." + i + ".addressLine2")} placeholder="Apt, suite, unit" />
                </div>
                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
                  <div className="form-group">
                    <label>Country</label>
                    <Select options={countryOptions} isClearable placeholder="Select country"
                      onChange={o => { setValue("addresses." + i + ".countryId", o?.value || null); setValue("addresses." + i + ".cityId", null); }} />
                  </div>
                  <div className="form-group">
                    <label>City</label>
                    <Select options={cityOptions} isClearable placeholder="Select city"
                      onChange={o => setValue("addresses." + i + ".cityId", o?.value || null)} />
                  </div>
                </div>
              </div>
            );
          })}
          <button type="button" className="btn btn-secondary"
            onClick={() => addAddr({ addressLine1: "", addressLine2: "", cityId: null, countryId: null })}>+ Add Address</button>
        </div>

        <div className="card" style={{ marginBottom: 24 }}>
          <h3 style={{ fontSize: 15, fontWeight: 600, marginBottom: 16, color: "#334155" }}>Family Members</h3>
          <Select isMulti options={allCustomers} value={selectedFamily} onChange={setSelectedFamily} placeholder="Search and select customers..." />
          <p style={{ fontSize: 12, color: "#94a3b8", marginTop: 8 }}>Only existing customers can be linked as family members.</p>
        </div>

        <div style={{ display: "flex", gap: 12 }}>
          <button type="submit" className="btn btn-primary" disabled={isSubmitting} style={{ padding: "10px 28px", fontSize: 15 }}>
            {isSubmitting ? "Saving..." : isEdit ? "Update Customer" : "Create Customer"}
          </button>
          <button type="button" className="btn btn-secondary" onClick={() => navigate(-1)}>Cancel</button>
        </div>
      </form>
    </div>
  );
}
