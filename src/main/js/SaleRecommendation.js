import React, { useState, useEffect } from "react";
import { Link } from "@reach/router";
import axios from "axios";
import Spinner from "react-bootstrap/Spinner";
import Container from "react-bootstrap/Container";

const SaleRecommendation = props => {
  const [result, setResult] = useState(null);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const targetUrl = `http://localhost:8080/api/risks/${props.riskId}/recommendations/sales/${props.investmentId}`;

  useEffect(() => {
    const fetchSaleRecommendation = async () => {
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

    fetchSaleRecommendation();
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
      <div>
        <table>
          <thead>
            <tr>
              <th className="header">WKN</th>
              <th className="header">Name</th>
              <th className="header">RSL</th>
              <th className="header">Preis</th>
              <th className="header">Preisschwelle</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td className="text-content">
                {result.saleRecommendations[0].wkn}
              </td>
              <td className="text-content">
                {result.saleRecommendations[0].name}
              </td>
              <td className="number-content">
                {result.saleRecommendations[0].rsl}
              </td>
              <td className="number-content">
                {result.saleRecommendations[0].price}
              </td>
              <td className="number-content">
                {result.saleRecommendations[0].notionalSalesPrice}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div>
        {result.saleRecommendations[0].shouldSellByFallingBelowTheLimit && (
          <p>
            {result.name} Wochenpreis {result.saleRecommendations[0].price}{" "}
            liegt unter dem kalkulierten Verkaufspreis{" "}
            {result.saleRecommendations[0].initialNotionalSalesPrice}.{" "}
            <Link
              to={`/riskManagements/${props.riskId}/purchase-recommendations`}
            >
              *
            </Link>
          </p>
        )}
        {result.saleRecommendations[0].shouldSellByRslComparison && (
          <p>
            {result.saleRecommendations[0].name} RSL{" "}
            {result.saleRecommendations[0].rsl} liegt unter dem{" "}
            {result.exchange} RSL {result.exchangeRsl}.{" "}
            <Link
              to={`/riskManagements/${props.riskId}/purchase-recommendations`}
            >
              *
            </Link>
          </p>
        )}
      </div>
    </div>
  );
};

export default SaleRecommendation;
