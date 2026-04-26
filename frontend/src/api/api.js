import axios from 'axios';

const api = axios.create({ baseURL: 'http://localhost:8080/api' });

export const customerApi = {
  getAll:   (params)     => api.get('/customers', { params }),
  getById:  (id)         => api.get(`/customers/${id}`),
  create:   (data)       => api.post('/customers', data),
  update:   (id, data)   => api.put(`/customers/${id}`, data),
  delete:   (id)         => api.delete(`/customers/${id}`),
};

export const masterDataApi = {
  get: () => api.get('/master-data'),
};

export const bulkUploadApi = {
  upload:    (formData) => api.post('/bulk-upload', formData),
  getStatus: (jobId)    => api.get(`/bulk-upload/status/${jobId}`),
};