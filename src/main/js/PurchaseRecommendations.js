import React, { useState, useEffect } from "react";
import { Container, Spinner, Table } from "react-bootstrap";
import axios from "axios";
import PurchaseRecommendation from "./PurchaseRecommendation";
import NavigationBar from "./NavigationBar";

const PurchaseRecommendations = ({ riskId }) => {
  const [result, setResult] = useState(null);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const targetUrl = `http://localhost:8080/api/risks/${riskId}/recommendations/purchases`;

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

  if (result.purchaseRecommendations.length === 0) {
    return (
      <>
        <NavigationBar riskManagementId={riskId} />
        <p>Alles schon gekauft - nichts zu empfehlen</p>
      </>
    );
  }

  return (
    <>
      <NavigationBar riskManagementId={riskId} />
      <Table striped bordered>
        <thead>
          <tr>
            <th className="header">WKN</th>
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
                riskManagementId={riskId}
                updateView={fetchData}
              />
            ))}
          <tr className="exchange-row">
            <td />
            <td className="text-content">{result.exchange}</td>
            <td className="number-content">{result.exchangeRsl.toFixed(2)}</td>
            <td colSpan={4} />
          </tr>
        </tbody>
      </Table>
    </>
  );
};

export default PurchaseRecommendations;
