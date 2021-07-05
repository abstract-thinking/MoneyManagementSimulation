import React from "react";

const Explanation = ({ entry }) => (
  <tr className="entry-data">
    <td className="text-content">{entry.symbol}</td>
    <td className="text-content">{entry.name}</td>
    <td className="number-content">{entry.rsl.toFixed(2)}</td>
    <td className="number-content">{entry.purchasePrice.toFixed(2)}</td>
    <td className="number-content">{entry.currentPrice.toFixed(2)}</td>
    <td className="number-content">{entry.currentStopPrice.toFixed(2)}</td>
    <td className="number-content">{entry.initialStopPrice.toFixed(2)}</td>
  </tr>
);

export default Explanation;
