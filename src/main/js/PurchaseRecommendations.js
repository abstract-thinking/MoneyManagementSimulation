import React, { useState, useEffect } from "react";
import { Table } from "react-bootstrap";
import axios from "axios";
import PurchaseRecommendation from "./PurchaseRecommendation";
import NavigationBar from "./NavigationBar";

const PurchaseRecommendations = ({ riskManagementId }) => {
  const [result, setResult] = useState(null);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const targetUrl = `http://localhost:9090/api/riskManagements/${riskManagementId}/recommendations/purchases`;

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

  if (result.purchaseRecommendations.length === 0) {
    return (
      <>
        <NavigationBar riskManagementId={riskManagementId} />
        <p className="position-absolute top-50 start-50 translate-middle fs-2">
          Alles interessante schon gekauft - keine Empfehlung vorhanden!
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
            <th className="header">Symbol</th>
            <th className="header">Name</th>
            <th className="header">RSL</th>
            <th className="header">Anzahl</th>
            <th className="header">Preis</th>
            <th className="header">Fiktiver Verkaufspreis</th>
          </tr>
        </thead>
        <tbody>
          {result.purchaseRecommendations &&
            result.purchaseRecommendations.map(purchaseRecommendation => (
              <PurchaseRecommendation
                key={purchaseRecommendation.name}
                purchaseRecommendation={purchaseRecommendation}
                riskManagementId={riskManagementId}
                updateView={fetchData}
              />
            ))}
          <tr className="exchange-row">
            <td />
            <td className="text-content">{result.exchangeResult.name}</td>
            <td className="number-content">{result.exchangeResult.rsl.toFixed(2)}</td>
            <td colSpan={4} />
          </tr>
        </tbody>
      </Table>
    </>
  );
};

export default PurchaseRecommendations;
