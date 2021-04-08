import React, { useState } from "react";
import { Table } from "react-bootstrap";
import axios from "axios";
import NavigationBar from "./NavigationBar";

const CalculationExplanation = ({ riskManagementId }) => {
  const [result, setResult] = useState(null);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");

  const targetUrl = `http://localhost:8080/api/riskManagements/${riskManagementId}/calc?wkn=`;

  const fetchData = () => {
    setLoading(true);
    setError("");

    console.log(targetUrl + searchTerm);
    axios
      .get(targetUrl + searchTerm)
      .then(response => {
        setResult(response.data);
        console.log("Received data: ", response.data);
        setLoading(false);
        setSearchTerm("");
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

  return (
    <>
      <NavigationBar riskManagementId={riskManagementId} />
      <form>
        <Table striped bordered>
          <tbody>
            <tr>
              <td className="header">WKN</td>
              <td>
                {" "}
                <input
                  type="search"
                  className="form-control"
                  placeholder="Search..."
                  value={searchTerm}
                  onChange={e => setSearchTerm(e.target.value)}
                />
              </td>
            </tr>
            <tr>
              <td />
              <td>
                {" "}
                <button
                  type="submit"
                  className="btn btn-primary"
                  onClick={() => fetchData()}
                >
                  Suchen
                </button>
              </td>
            </tr>
          </tbody>
        </Table>
      </form>
      <br />
      {result && (
        <Table striped bordered>
          <tbody>
            <tr key={result.name}>
              <td>WKN</td>
              <td className="text-content">{result.wkn}</td>
            </tr>
            <tr>
              <td>Name</td>
              <td className="text-content">{result.name}</td>
            </tr>
            <tr>
              <td>RSL</td>
              <td>{result.rsl}</td>
            </tr>
            <tr>
              <td>Aktueller Kurs</td>
              <td>{result.price.toFixed(2)}</td>
            </tr>
            <tr>
              <td>Börsen RSL</td>
              <td>{result.exchangeRsl}</td>
            </tr>
            <tr>
              <td>Fiktiver Stoppkurs</td>
              <td>{result.notionalSalesPrice.toFixed(2)}</td>
            </tr>
            <tr>
              <td>Relatives Positionsrisiko</td>
              <td>{result.positionRisk.toFixed(2)}</td>
            </tr>
            <tr>
              <td>Gesamte Transaktionskosten</td>
              <td>{result.transactionCosts.toFixed(2)}</td>
            </tr>
            <tr>
              <td>Absolutes Positionsrisiko</td>
              <td>
                {(result.positionRisk - result.transactionCosts).toFixed(2)}
              </td>
            </tr>
            <tr>
              <td>Positionsrisiko pro Stück</td>
              <td>{(result.price - result.notionalSalesPrice).toFixed(2)}</td>
            </tr>
            <tr>
              <td>Anzahl</td>
              <td>{result.quantity}</td>
            </tr>
          </tbody>
        </Table>
      )}
    </>
  );
};

export default CalculationExplanation;
