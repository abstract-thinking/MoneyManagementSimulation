import React from "react";

const OneViewEntry = ({ entry }) => (
  <tr className="entry-data">
    <td className="text-content">{entry.wkn}</td>
    <td className="text-content">{entry.name}</td>
    <td className="number-content">{entry.rsl.toFixed(2)}</td>
    <td className="number-content">{entry.price.toFixed(2)}</td>
    <td className="number-content">{entry.stopPrice.toFixed(2)}</td>
  </tr>
);

export default OneViewEntry;
