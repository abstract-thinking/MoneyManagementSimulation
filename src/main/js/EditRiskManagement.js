import React, { useState, useEffect } from "react";
import axios from "axios";

import NavigationBar from "./NavigationBar";
import Investment from "./Investment";

const EditRiskManagement = () => {
  const [riskManagement, setRiskManagement] = useState("");
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const riskManagementId = "1";

  useEffect(() => {
    setLoading(true);
    setError("");

    axios
      .get("http://localhost:8080/api/risks/" + riskManagementId)
      .then(response => {
        setRiskManagement(response.data);
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
    return <p>loading..</p>;
  }

  if (error !== "") {
    return <p>ERROR: {error}</p>;
  }

  if (riskManagement === "") {
    return <p>loading starts now</p>;
  }

  return (
    <div className="container">
      <NavigationBar riskManagementId={riskManagementId} />
      <form>
        <div className="header">Gesamtkapital</div>
        <input
          id="total_capital"
          value={riskManagement.totalCapital.toFixed(2)}
        />
        <div className="header">Einzelpositionrisiko</div>
        <input
          id="position_risk"
          value={riskManagement.individualPositionRiskInPercent.toFixed(2)}
        />

        <table>
          <thead>
            <tr>
              <th className="header">WKN</th>
              <th className="header">Name</th>
              <th className="header">Stück</th>
              <th className="header">Kaufkurs</th>
              <th className="header">Einstiegssumme</th>
              <th className="header">Fiktiver Verkaufspreis</th>
              <th className="header">Kosten</th>
              <th className="header">Fiktiver Verkaufserlös</th>
              <th className="header">Risiko</th>
            </tr>
          </thead>
          <tbody>
            {riskManagement.investments &&
              riskManagement.investments.map(investment => (
                <tr key={investment.wkn} className="investment-data">
                  <td className="text-content">{investment.wkn}</td>
                  <td className="text-content">{investment.name}</td>
                  <td className="number-content">{investment.quantity}</td>
                  <td className="number-content">
                    {investment.purchasePrice.toFixed(2)}
                  </td>
                  <td className="number-content">
                    {investment.investment.toFixed(2)}
                  </td>
                  <td className="number-content">
                    {investment.notionalSalesPrice.toFixed(2)}
                  </td>
                  <td className="number-content">
                    {investment.transactionCosts.toFixed(2)}
                  </td>
                  <td className="number-content">
                    {investment.notionalRevenue.toFixed(2)}
                  </td>
                  <td className="number-content">
                    {investment.positionRisk.toFixed(2)}
                  </td>
                  <td>
                    <button>-</button>
                  </td>
                </tr>
              ))}

            <tr className="investment-data">
              <td className="text-content">
                <input />
              </td>
              <td className="text-content">
                <input />
              </td>
              <td className="number-content">
                <input />
              </td>
              <td className="number-content">
                <input />
              </td>
              <td className="number-content">
                <input />
              </td>
              <td className="number-content">
                <input />
              </td>
              <td className="number-content">
                <input />
              </td>
              <td className="number-content">
                <input />
              </td>
              <td className="number-content">
                <input />
              </td>
              <td>
                <button>+</button>
              </td>
            </tr>
          </tbody>
        </table>
      </form>
    </div>
  );
};

export default EditRiskManagement;
