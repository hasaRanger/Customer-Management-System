import React, { createContext, useContext, useEffect, useState } from 'react';
import { masterDataApi } from '../api/api';

const MasterDataContext = createContext(null);

export function MasterDataProvider({ children }) {
  const [countries, setCountries] = useState([]);
  const [cities, setCities]       = useState([]);
  const [loading, setLoading]     = useState(true);

  useEffect(() => {
    masterDataApi.get().then(r => {
      setCountries(r.data.countries);
      setCities(r.data.cities);
    }).finally(() => setLoading(false));
  }, []);

  return (
    <MasterDataContext.Provider value={{ countries, cities, loading }}>
      {children}
    </MasterDataContext.Provider>
  );
}

export const useMasterData = () => useContext(MasterDataContext);