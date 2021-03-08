import React, { useState, useEffect } from "react";
import axios from "axios";
import PurchaseRecommendation from "./PurchaseRecommendation";
import Spinner from "react-bootstrap/Spinner";
import Container from "react-bootstrap/Container";


const PurchaseRecommendations = props => {
  const [result, setResult] = useState(null);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const targetUrl = `http://localhost:8080/api/risks/${props.riskId}/recommendations/purchases`;

  useEffect(() => {
    const fetchPurchaseRecommendations = async () => {
      try {
        setLoading(true);
        setError("");

        const response = await axios(targetUrl);
        console.log("Received data: ", response.data);
        setResult(response.data);
      } catch (err) {
        setError(err);
      }
      setLoading(false);
    };

    fetchPurchaseRecommendations();
  }, [targetUrl]);

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
    <div className="container">
      <table>
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
                showPurchaseButton="true"
              />
            ))}
          <tr className="exchange-row">
            <td />
            <td className="text-content">{result.exchange}</td>
            <td className="number-content">{result.exchangeRsl.toFixed(2)}</td>
            <td colSpan={4} />
          </tr>
        </tbody>
      </table>
    </div>
  );
};

export default PurchaseRecommendations;
