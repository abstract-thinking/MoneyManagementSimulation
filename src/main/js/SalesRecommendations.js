import React, { useState, useEffect } from "react";
import axios from "axios";

import SaleRecommendation from "./SaleRecommendation";
import { Table } from "react-bootstrap";
import NavigationBar from "./NavigationBar";

const SalesRecommendations = ({ riskManagementId }) => {
  const [result, setResult] = useState(null);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const targetUrl = `http://localhost:8080/api/riskManagements/${riskManagementId}/recommendations/sales`;

  useEffect(() => {
    fetchData();
  }, [targetUrl]);

  const fetchData = () => {
    setLoading(true);
    setError("");

    axios
      .get(targetUrl)
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

  if (result === null) {
    return <p>loading starts now</p>;
  }

  if (result.saleRecommendations.length === 0) {
    return (
      <>
        <NavigationBar riskManagementId={riskManagementId} />
        <p className="position-absolute top-50 start-50 translate-middle fs-2">
          Zur Zeit keine Empfehlung vorhanden!
        </p>
      </>
    );
  }

  return (
    <>
      <NavigationBar riskManagementId={riskManagementId} />
      <Table striped bordered>
        <thead>
          <tr>
            <th className="header">WKN</th>
            <th className="header">Name</th>
            <th className="header">RSL</th>
            <th className="header">Preis</th>
            <th className="header">Fiktiver Verkaufspreis</th>
            <th className="header">Begründung</th>
          </tr>
        </thead>
        <tbody>
          {result.saleRecommendations &&
            result.saleRecommendations.map(saleRecommendation => (
              <SaleRecommendation
                key={saleRecommendation.name}
                riskManagementId={riskManagementId}
                saleRecommendation={saleRecommendation}
                exchange={result.exchangeResult}
                updateView={fetchData}
              />
            ))}
        </tbody>
      </Table>
    </>
  );
};

export default SalesRecommendations;
