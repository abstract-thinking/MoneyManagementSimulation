import React, { useState, useEffect } from "react";
import { Container, Spinner } from "react-bootstrap";
import axios from "axios";
import NavigationBar from "./NavigationBar";

const RiskManagementModifier = ({ riskId }) => {
  const [result, setResult] = useState(null);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [data, setData] = useState(null);

  const targetUrl = `http://localhost:8080/api/risks/${riskId}`;

  useEffect(() => {
    fetchData();
  }, [targetUrl]);

  const fetchData = () => {
    setLoading(true);
    setError("");

    axios
      .get(targetUrl)
      .then(response => {
        console.log("Received data: ", response.data);
        setResult(response.data);
        setData({
          totalCapital: response.data.totalCapital,
          individualPositionRiskInPercent:
            response.data.individualPositionRiskInPercent
        });
        setLoading(false);
      })
      .catch(error => {
        console.log("Error: ", error);

        setLoading(false);
        setError("Error: " + error);
      });
  };

  const updateData = () => {
    setLoading(true);
    setError("");

    axios
      .put(targetUrl, data)
      .then(response => {
        setResult(response.data);
        console.log("Received data: ", response.data);
        setLoading(false);
      })
      .catch(error => {
        console.log("Error: ", error);

        setLoading(false);
        setError("Error: " + error);
      });
  };

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

  if (result === null) {
    return <p>loading starts now</p>;
  }

  return (
    <>
      <NavigationBar riskManagementId={riskId} />
      <form className="row g-3">
        <div className="col-auto">
          <label htmlFor="search">Gesamtkapital</label>
          <input
            type="text"
            value={data.totalCapital}
            onChange={e => setData({ ...data, totalCapital: e.target.value })}
          />
        </div>
        <div className="col-auto">
          <label htmlFor="search">Einzelpositionrisiko</label>
          <input
            type="text"
            value={data.individualPositionRiskInPercent}
            onChange={e =>
              setData({
                ...data,
                individualPositionRiskInPercent: e.target.value
              })
            }
          />
          <input
            type="text"
            value={
              (data.totalCapital * data.individualPositionRiskInPercent) / 100
            }
            readOnly
          />
        </div>

        <div className="col-auto">
          <button
            type="submit"
            className="btn btn-primary mb-3"
            onClick={() => updateData()}
          >
            Ändern
          </button>
        </div>
      </form>
    </>
  );
};

export default RiskManagementModifier;
