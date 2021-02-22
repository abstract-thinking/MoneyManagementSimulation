import React, { useState, useEffect } from "react";
import axios from "axios";
import PurchaseRecommendation from "./PurchaseRecommendation";

const SalesRecommendations = props => {
  const [salesRecommendation, setSalesRecommendation] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const targetUrl = `http://localhost:8080/api/risks/${props.riskId}/recommendations/sales`;

  useEffect(() => {
    const fetchSalesRecommendations = async () => {
      try {
        setLoading(true);
        setError("");

        const response = await axios(targetUrl);
        console.log("Received data: ", response.data);
        setSalesRecommendation(response.data);
      } catch (err) {
        setError(err);
      }
      setLoading(false);
    };

    console.log(targetUrl);
    fetchSalesRecommendations();
  }, [targetUrl]);

  if (loading) {
    return <p>loading..</p>;
  }

  if (error !== "") {
    return <p>ERROR: {error}</p>;
  }

  if (salesRecommendation.length == 0) {
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
              <th className="header">Preisschwelle</th>
              <th className="header">Begründung</th>
            </tr>
          </thead>
          <tbody>
            {salesRecommendation.map(saleRecommendation => {
              return (
                <tr key={saleRecommendation.company}>
                  <td className="text-content">{saleRecommendation.wkn}</td>
                  <td className="text-content">{saleRecommendation.name}</td>
                  <td className="number-content">
                    {saleRecommendation.rsl}
                  </td>
                  <td className="number-content">{saleRecommendation.price}</td>
                  <td className="number-content">
                    {saleRecommendation.initialNotionalSalesPrice}
                  </td>
                  <td>
                    {saleRecommendation.shouldSellByFallingBelowTheLimit ? (
                      <div>
                        {saleRecommendation.company} wöchentlicher Schlusskurs{" "}
                        {saleRecommendation.price} liegt unter dem kalkulierten
                        Verkaufspreis{" "}
                        {saleRecommendation.initialNotionalSalesPrice}.
                      </div>
                    ) : null}
                    {saleRecommendation.shouldSellByRslComparison ? (
                      <div>
                        {saleRecommendation.company} RSL{" "}
                        {saleRecommendation.companyRsl} liegt unter dem RSL{" "}
                        {saleRecommendation.exchangeRsl}
                      </div>
                    ) : null}
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
