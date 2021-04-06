import React, { useState, useEffect } from "react";
import { Table } from "react-bootstrap";
import axios from "axios";
import Investment from "./Investment";
import NavigationBar from "./NavigationBar";

const RiskManagement = ({ riskManagementId }) => {
  const [riskManagement, setRiskManagement] = useState("");
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const targetUrl = `http://localhost:8080/api/riskManagements/${riskManagementId}`;

  useEffect(() => {
    setLoading(true);
    setError("");

    axios
      .get(targetUrl)
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

  if (riskManagement === "") {
    return <p>loading starts now</p>;
  }

  if (riskManagement.investments.length === 0) {
    return (
      <>
        <NavigationBar riskManagementId={riskManagementId} />
        <p className="position-absolute top-50 start-50 translate-middle fs-2">
          Zur Zeit keine Investment vorhanden!
        </p>
      </>
    );
  }

  return (
    <>
      <NavigationBar riskManagementId={riskManagementId} />
      <Table striped bordered>
        <tbody>
          <tr>
            <td className="header">Gesamtkapital</td>
            <td>{riskManagement.totalCapital.toFixed(2)}</td>
          </tr>
          <tr>
            <td className="header">Einzelpositionrisiko</td>
            <td>{riskManagement.individualPositionRiskInPercent.toFixed(2)}</td>
          </tr>
          <tr>
            <td />
            <td>{riskManagement.individualPositionRisk.toFixed(2)}</td>
          </tr>
          <tr>
            <td className="header">WKN</td>
            <td className="header">Name</td>
            <td className="header">Stück</td>
            <td className="header">Kaufkurs</td>
            <td className="header">Einstiegssumme</td>
            <td className="header">Fiktiver Verkaufspreis</td>
            <td className="header">Kosten</td>
            <td className="header">Fiktiver Verkaufserlös</td>
            <td className="header">Positionsrisiko</td>
          </tr>
          {riskManagement.investments &&
            riskManagement.investments.map(investment => (
              <Investment
                key={investment.name}
                riskManagementId={riskManagementId}
                investment={investment}
              />
            ))}
          <tr>
            <td className="header">Depotwert</td>
            <td colSpan={3} />
            <td className="number-content">
              {riskManagement.totalInvestment.toFixed(2)}
            </td>
            <td colSpan={2} />
            <td className="number-content">
              {riskManagement.totalRevenue.toFixed(2)}
            </td>
            <td className="number-content">
              {riskManagement.depotRisk.toFixed(2)}
            </td>
          </tr>
          <tr>
            <td className="header">Depotrisiko</td>
            <td colSpan={7} />
            <td className="number-content">
              {riskManagement.depotRiskInPercent.toFixed(2)}
            </td>
          </tr>
          <tr>
            <td className="header">Gesamtrisiko</td>
            <td className="number-content">
              {riskManagement.totalRiskInPercent.toFixed(2)}
            </td>
          </tr>
        </tbody>
      </Table>
    </>
  );
};

export default RiskManagement;
