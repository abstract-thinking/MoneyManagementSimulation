import React, { useState, useEffect } from "react";
import { Link } from "@reach/router";
import axios from "axios";

const SaleRecommendation = props => {
  const [saleRecommendation, setSaleRecommendation] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const targetUrl = `http://localhost:8080/api/risks/${props.riskId}/recommendations/sales/${props.investmentId}`;

  useEffect(() => {
    const fetchSaleRecommendation = async () => {
      try {
        setLoading(true);
        setError("");

        const response = await axios(targetUrl);
        console.log("Received data: ", response.data);
        setSaleRecommendation(response.data);
      } catch (err) {
        setError(err);
      }
      setLoading(false);
    };

    console.log(targetUrl);
    fetchSaleRecommendation();
  }, [targetUrl]);

  if (loading) {
    return <p>loading..</p>;
  }

  if (error !== "") {
    return <p>ERROR: {error}</p>;
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
              <th className="header">Börse RSL</th>
              <th className="header">RSL</th>
              <th className="header">Preis</th>
              <th className="header">Preisschwelle</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td className="text-content">{saleRecommendation.wkn}</td>
              <td className="text-content">{saleRecommendation.company}</td>
              <td className="text-content">{saleRecommendation.exchange}</td>
              <td className="number-content">{saleRecommendation.exchangeRsl}</td>
              <td className="number-content">{saleRecommendation.companyRsl}</td>
              <td className="number-content">{saleRecommendation.price}</td>
              <td className="number-content">
                {saleRecommendation.initialNotionalSalesPrice}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div>
        {saleRecommendation.shouldSellByFallingBelowTheLimit ? (
          <p>
            {saleRecommendation.company} Wochenpreis {saleRecommendation.price}{" "}
            liegt unter dem kalkulierten Verkaufspreis{" "}
            {saleRecommendation.initialNotionalSalesPrice}.{" "}
            <Link
              to={`/riskManagements/${props.riskId}/purchase-recommendations`}
            >
              *
            </Link>
          </p>
        ) : null}
        {saleRecommendation.shouldSellByRslComparison ? (
          <p>
            {saleRecommendation.company} RSL {saleRecommendation.companyRsl}{" "}
            liegt unter dem {saleRecommendation.exchange} RSL{" "}
            {saleRecommendation.exchangeRsl}.{" "}
            <Link
              to={`/riskManagements/${props.riskId}/purchase-recommendations`}
            >
              *
            </Link>
          </p>
        ) : null}
      </div>
    </div>
  );
};

export default SaleRecommendation;
