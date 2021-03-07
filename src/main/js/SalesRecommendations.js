import React, { useState, useEffect } from "react";
import axios from "axios";
import Spinner from "react-bootstrap/Spinner";
import Container from "react-bootstrap/Container";


const SalesRecommendations = props => {
  const [result, setResult] = useState(null);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const targetUrl = `http://localhost:8080/api/risks/${props.riskId}/recommendations/sales`;

  useEffect(() => {
    const fetchSalesRecommendations = async () => {
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

    fetchSalesRecommendations();
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

  if (result.saleRecommendations.length === 0) {
    return (
      <div className="container">
        <p>Keine Verkaufsempfehlung</p>
      </div>
    );
  }

  return (
    <div className="container">
      <div>
        <table>
          <thead>
            <tr>
              <th className="header">WKN</th>
              <th className="header">Name</th>
              <th className="header">Börse</th>
              <th className="header">Preis</th>
              <th className="header">Fiktiver Verkaufspreis</th>
              <th className="header">Begründung</th>
            </tr>
          </thead>
          <tbody>
            {result.saleRecommendations.map(saleRecommendation => {
              return (
                <tr key={saleRecommendation.company}>
                  <td className="text-content">{saleRecommendation.wkn}</td>
                  <td className="text-content">{saleRecommendation.name}</td>
                  <td className="number-content">{saleRecommendation.rsl}</td>
                  <td className="number-content">{saleRecommendation.price}</td>
                  <td className="number-content">
                    {saleRecommendation.initialNotionalSalesPrice}
                  </td>
                  <td>
                    <button>-</button>
                  </td>
                  <td>
                    {saleRecommendation.shouldSellByFallingBelowTheLimit && (
                      <div>
                        {saleRecommendation.name} wöchentlicher Schlusskurs{" "}
                        {saleRecommendation.price} liegt unter dem kalkulierten
                        Verkaufspreis{" "}
                        {saleRecommendation.initialNotionalSalesPrice}.
                      </div>
                    )}
                    {saleRecommendation.shouldSellByRslComparison && (
                      <div>
                        {saleRecommendation.name} RSL {result.rsl} liegt unter
                        dem RSL {result.exchangeRsl}
                      </div>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default SalesRecommendations;
