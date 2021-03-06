import React, { useState, useEffect } from "react";
import axios from "axios";
import { navigate } from "@reach/router";

const RiskManagementChooser = () => {
  const [riskManagements, setRiskManagements] = useState(null);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    setError("");

    axios
      .get("http://localhost:9090/api/riskManagements/")
      .then(response => {
        setRiskManagements(response.data);
        console.log("Received data: ", response.data);
        setLoading(false);
      })
      .catch(error => {
        console.log("Error: ", error);

        setLoading(false);
        setError("Error: " + error);
      });
  }, []);

  if (isLoading) {
    return (
      <div
        className="position-absolute top-50 start-50 translate-middle spinner-grow"
        role="status"
      >
        <span className="visually-hidden">Loading...</span>
      </div>
    );
  }

  if (error !== "") {
    return <p>ERROR: {error}</p>;
  }

  if (riskManagements === null) {
    return <p>loading starts now</p>;
  }

  if (riskManagements.riskResults.length === 1) {
    navigate(`/riskManagements/${riskManagements.riskResults[0].id}`);
    return;
  }

  return (
    <div
      className="position-absolute top-50 start-50 translate-middle btn-group-vertical"
      role="group"
      aria-label="Basic outlined"
    >
      {riskManagements.riskResults.map(riskManagement => {
        return (
          <button
            type="button"
            className="btn btn-outline-primary"
            key={riskManagement.id}
            onClick={() => navigate(`/riskManagements/${riskManagement.id}`)}
          >
            {riskManagement.name}
          </button>
        );
      })}
    </div>
  );
};

export default RiskManagementChooser;
