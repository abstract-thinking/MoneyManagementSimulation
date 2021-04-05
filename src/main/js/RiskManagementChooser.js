import React, { useState, useEffect } from "react";
import { Container, Spinner } from "react-bootstrap";
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
      .get("http://localhost:8080/api/risks/")
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
      <Container
        className="d-flex align-items-center justify-content-center"
        style={{ minHeight: "100vh" }}
      >
        <Spinner animation="grow" role="status">
          <span className="sr-only">Loading...</span>
        </Spinner>
      </Container>
    );
  }

  if (error !== "") {
    return <p>ERROR: {error}</p>;
  }

  if (riskManagements === null) {
    return <p>loading starts now</p>;
  }

  if (riskManagements.riskResults.length === 1) {
    navigate(`/riskManagements/${riskManagement.id}`);
    return;
  }

  return (
    <div className="btn-group-vertical" role="group" aria-label="Basic example">
      {riskManagements.riskResults.map(riskManagement => {
        return (
          <button
            type="button"
            className="btn btn-primary"
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
